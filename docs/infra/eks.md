# EKS

**Cluster:** `eks-store`  
**Região:** `us-east-2`  
**Nodes:** 2x `t3.medium`

---

## Criação do cluster

### 1. Role IAM para o cluster

Criar role com a policy `AmazonEKSClusterPolicy` e trust relationship para `eks.amazonaws.com`.

### 2. Criar cluster EKS

No console AWS → EKS → Create cluster:

- Nome: `eks-store`
- VPC: `eks-vpc` (criada via CloudFormation)
- Subnets: selecionar as 4 subnets (públicas e privadas)

### 3. Role para o Node Group

Criar role com as policies:

- `AmazonEKSWorkerNodePolicy`
- `AmazonEKS_CNI_Policy`
- `AmazonEC2ContainerRegistryReadOnly`

### 4. Criar Node Group

- Tipo de instância: `t3.medium`
- Tamanho: mínimo 1, desejado 2, máximo 3
- Subnets: apenas as **privadas**

### 5. Configurar kubectl

```bash
aws eks update-kubeconfig --region us-east-2 --name eks-store
kubectl config get-contexts
```

---

## Deploy dos serviços

O script `apply.ps1` na raiz do repositório aplica todos os manifests na ordem correta:

```powershell
.\apply.ps1
```

Ordem de deploy:

1. Secret (`app-secrets` do `.env`)
2. StorageClass `ebs-sc`
3. RabbitMQ
4. Redis
5. Account
6. Auth
7. Order
8. Product
9. Exchange
10. Gateway

---

## Manifests Kubernetes

Cada serviço possui um `k8s/k8s.yaml` com:

- `ConfigMap` — variáveis de ambiente não-sensíveis
- `PersistentVolumeClaim` — para serviços com banco de dados (StorageClass `ebs-sc`)
- `Deployment` — configuração do pod com limites de recursos
- `Service` — exposição interna via ClusterIP

### Recursos configurados

| Serviço | CPU request | Memory request |
|---------|-------------|----------------|
| order | 250m | 512Mi |
| product | 250m | 512Mi |
| exchange | 100m | 256Mi |
| account | 250m | 512Mi |
| auth | 250m | 512Mi |
| gateway | 250m | 512Mi |
| order-db | 100m | 256Mi |
| product-db | 100m | 256Mi |
| rabbitmq | 100m | 256Mi |
| redis | 50m | 128Mi |

---

## Verificar estado do cluster

```bash
# listar todos os pods
kubectl get pods

# listar serviços
kubectl get services

# logs de um pod específico
kubectl logs deployment/order --tail=50

# descrever um pod com erro
kubectl describe pod <nome-do-pod>
```

---

## StorageClass EBS

Para persistência dos bancos PostgreSQL no EKS, foi criada a StorageClass `ebs-sc`:

```yaml
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
  name: ebs-sc
provisioner: ebs.csi.aws.com
volumeBindingMode: WaitForFirstConsumer
```

Cada banco usa um `PersistentVolumeClaim` de 5Gi com essa StorageClass.
