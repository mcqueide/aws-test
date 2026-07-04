# Configure EC2 machine

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