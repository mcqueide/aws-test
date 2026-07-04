# Configure EC2 machine

ec2: my-ec2
key-pair: my-ec2-keypair
sg: my-sg

ssh client: 
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