## Split Wise App Deployment Guide

This guide provides step-by-step instructions for building, deploying, and managing the Split Wise App using Docker
containers and AWS services.

### Docker Setup

Building and Pushing Docker Image

```bash
docker build -t split-wise-app .
docker tag split-wise-app niteenjava/split-wise-app
docker push niteenjava/split-wise-app

docker images
docker ps -a
docker rmi <image-name>
docker rm <container-name>
```

### AWS Setup

Installing AWS CLI

```bash
curl "https://awscli.amazonaws.com/AWSCLIV2.pkg" -o "AWSCLIV2.pkg"
sudo installer -pkg AWSCLIV2.pkg -target /
which aws
aws --version
```

Verify AWS CLI installation

```bash
which aws
aws --version
```

Configuring AWS CLI

```bash
aws configure
```

Docker Registry Authentication with ECR

```bash
aws ecr get-login-password --region us-east-2 | docker login --username AWS --password-stdin 544180684050.dkr.ecr.us-east-2.amazonaws.com
```

### Amazon EKS Setup

Creating EKS Cluster

```bash
eksctl create cluster --name nc-cluster --version 1.28 --nodes=1 --node-type=t2.small --region us-east-2
```

Update kube-config to connect to the newly created EKS cluster:

```bash
aws eks --region us-east-2 update-kubeconfig --name nc-cluster
```

### Deploying Kubernetes Resources

Create the deployment, service, and configmap YAML files and apply them:

```bash
kubectl apply -f configmap.yml
kubectl apply -f k8s.yml
kubectl apply -f service.yml
```

Verification

```bash
kubectl config set-context --current --namespace=split-wise-namespace
kubectl get deployment
kubectl get pod
kubectl logs <pod-name>
kubectl describe pod <pod-name>
kubectl get service
```

Delete the EKS cluster

deleting the cluster takes some time to delete node groups and the cluster itself.

```bash
aws eks list-clusters
aws eks list-nodegroups --cluster-name <your-cluster-name>
aws eks delete-nodegroup --cluster-name <your-cluster-name> --nodegroup-name <your-nodegroup-name>
aws eks delete-cluster --name <your-cluster-name>
aws eks describe-cluster --name <your-cluster-name>
```