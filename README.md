# Install AWS CLI V2

```
# inside container (Debian/Ubuntu base)
apt-get update && apt-get install -y curl unzip
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "/tmp/awscliv2.zip"
unzip /tmp/awscliv2.zip -d /tmp
/tmp/aws/install

aws --version
```

# Run in EC2

## Configure EC2 machine

ec2: my-ec2

key-pair: my-ec2-keypair

sg: my-sg

## ssh client

chmod 400 "my-ec2-keypair.pem"

ssh -i "my-ec2-keypair.pem" ec2-user@ec2-instance-dns

## EC2 User data

```
#!/bin/bash

# Install java, maven and git
sudo dnf update -y
sudo dnf install -y java-21-amazon-corretto-devel maven git

# Clone git project
git clone https://github.com/mcqueide/aws-test.git /home/ec2-user/aws-test

# Update environment variables
cat >> /home/ec2-user/.bashrc <<'EOF'
export JAVA_HOME=/usr/lib/jvm/java-21-amazon-corretto
export PATH=$JAVA_HOME/bin:$PATH
EOF

# Reload shell config
source ~/.bashrc
```

## S3 bucket

Create s3 bucket


## Run the project
```
mvn spring-boot:run
```

## Set up credentials
```
# Using access key
aws configure --profile whizlabs

# Using temp access
aws login --profile {profileName}
```

## Set enviroment variables

```
export AWS_PROFILE={profileName}
export AWS_REGION=us-east-1
```

## Create a Role Policy

```
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "ListAllBuckets",
      "Effect": "Allow",
      "Action": "s3:ListAllMyBuckets",
      "Resource": "*"
    },
    {
      "Sid": "ListSpecificBucket",
      "Effect": "Allow",
      "Action": "s3:ListBucket",
      "Resource": "arn:aws:s3:::aws-test-bucket-johnm"
    },
    {
      "Sid": "ObjectReadWriteDelete",
      "Effect": "Allow",
      "Action": [
        "s3:GetObject",
        "s3:PutObject",
        "s3:DeleteObject"
      ],
      "Resource": "arn:aws:s3:::aws-test-bucket-johnm/*"
    }
  ]
}
```

# Run in EC2

## Build a Docker image

Build the image locally:

```bash
docker build -t aws-test .
```

Run it locally:

```bash
docker compose up -d
```

If you are testing against AWS from your machine, use your normal AWS credentials locally. Do not put access keys in the image.

## Push the image to ECR

Create a repository once:

```bash
aws ecr create-repository --repository-name aws-test
```

Log in Docker to ECR:

```bash
aws ecr get-login-password --region us-east-1 \
  | docker login --username AWS --password-stdin <account-id>.dkr.ecr.us-east-1.amazonaws.com/aws-test
```

Tag and push:

```bash
docker tag aws-test:latest <account-id>.dkr.ecr.us-east-1.amazonaws.com/aws-test:latest
docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/aws-test:latest
```

## Run it in ECS

Keep it simple and use ECS Fargate.

1. Create an ECS cluster.
2. Create a task definition using the ECR image.
3. Set container port to `8080`.
4. Add environment variables:
   - `AWS_REGION=us-east-1`
   - `SPRING_DATASOURCE_URL=jdbc:postgresql://<rds-endpoint>:5432/people`
   - `SPRING_DATASOURCE_USERNAME=<db-user>`
   - `SPRING_DATASOURCE_PASSWORD=<db-password>`
5. Attach a task role with your S3 permissions.
6. Attach an execution role with standard ECS permissions so ECS can pull from ECR and write logs to CloudWatch.
7. Create a security group for the service:
   - allow outbound internet access if needed
   - allow outbound access to S3 through NAT or a VPC endpoint
   - allow outbound access to the RDS port
8. Update the RDS security group to allow inbound `5432` from the ECS service security group.
9. Create the ECS service and start one task.

Important notes:

- In ECS, the app should get AWS credentials from the task role automatically.
- Do not set `AWS_PROFILE` in ECS.
- Do not bake credentials into the image.
- If you want a public test endpoint, place the ECS service behind an Application Load Balancer and forward traffic to port `8080`.

## Minimal IAM idea for ECS

Use two roles:

- Execution role: lets ECS pull the image from ECR and send logs to CloudWatch.
- Task role: lets your application call S3.

Your existing S3 policy can be attached to the task role.

## What to expect when testing

If the app works on EC2 with an instance role, the ECS equivalent is the task role. That is the main concept to verify.

Useful checks:

- Confirm the task starts and stays healthy.
- Confirm the app can connect to RDS.
- Confirm S3 calls work without any local profile or access key configuration.

# Run in EKS

## Kubernetes manifests in this project

- `k8s/namespace.yaml`: namespace for the app.
- `k8s/local/postgres.yaml`: local PostgreSQL deployment and service.
- `k8s/local/app-local.yaml`: app deployment for local testing using local PostgreSQL.
- `k8s/aws/app-eks-rds.yaml`: app deployment for EKS using RDS.

## Local Kubernetes test (app + local DB)

This is a simple way to test concepts locally with one app pod and one PostgreSQL pod.

Build your app image:

```bash
docker build -t aws-test:latest .
```

If you use Kind, load the image into your Kind cluster runtime:

```bash
kind load docker-image aws-test:latest
```

If your cluster name is not `kind`, specify it explicitly:

```bash
kind load docker-image aws-test:latest --name <your-kind-cluster-name>
```

Apply manifests:

```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/local/postgres.yaml
kubectl apply -f k8s/local/app-local.yaml
```

Check pods:

```bash
kubectl get pods -n aws-test
```

Port-forward app and test:

```bash
kubectl port-forward -n aws-test svc/aws-test 8080:8080
```

## Set up EKS for kubectl

Keep the first test simple and use `eksctl` to create the cluster.

Install `eksctl` on Linux:

```bash
curl --silent --location \
  "https://github.com/eksctl-io/eksctl/releases/latest/download/eksctl_$(uname -s)_amd64.tar.gz" \
  | tar xz -C /tmp

sudo mv /tmp/eksctl /usr/local/bin
eksctl version
```

Install the tools you need on your machine:

```bash
aws --version
kubectl version --client
eksctl version
```

Set your AWS region:

```bash
export AWS_REGION=us-east-1
```

Create a simple EKS cluster:

```bash
eksctl create cluster \
  --name aws-test-cluster \
  --region $AWS_REGION \
  --nodes 2
```

If your kubeconfig is not updated automatically, update it explicitly:

```bash
aws eks update-kubeconfig --region $AWS_REGION --name aws-test-cluster
```

Confirm that `kubectl` is pointing to the EKS cluster:

```bash
kubectl config current-context
kubectl get nodes
```

After that, `kubectl apply` commands will run against your EKS cluster.

## EKS test (app + RDS)

For EKS, use RDS instead of local PostgreSQL.

1. Push your image to ECR.
2. Update placeholders in `k8s/aws/app-eks-rds.yaml`:
  - `<account-id>`
  - `<region>`
  - `<rds-endpoint>`
  - `<db-user>`
  - `<db-password>`
3. Apply manifests:

```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/aws/app-eks-rds.yaml
```

4. Check service and external endpoint:

```bash
kubectl get svc -n aws-test
kubectl get pods -n aws-test
```

## IAM concept in EKS (simple)

For S3 access in EKS, use pod-level IAM (IRSA) so your app gets credentials automatically, similar to ECS task role behavior.

- Keep AWS credentials out of the container image.
- Do not set AWS profile inside the pod.

## Cleanup

```bash
kubectl delete namespace aws-test
```

