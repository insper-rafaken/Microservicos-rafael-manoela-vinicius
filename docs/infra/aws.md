# AWS

**Região:** `us-east-2` (Ohio)  
**Conta:** SSO Insper

---

## Configuração inicial

### 1. Criar usuário IAM

Criar um usuário IAM dedicado (não usar root) com permissões de:

- `AmazonEKSClusterPolicy`
- `AmazonEKSWorkerNodePolicy`
- `AmazonEC2ContainerRegistryReadOnly`
- `AmazonEC2FullAccess`

### 2. Criar Access Key

No console IAM → Security credentials → Create access key.

### 3. Configurar AWS CLI

```bash
aws configure
# AWS Access Key ID: <sua key>
# AWS Secret Access Key: <sua secret>
# Default region name: us-east-2
# Default output format: json
```

As credenciais ficam em `~/.aws/credentials`. **Nunca commitar esse arquivo.**

### 4. Verificar conexão

```bash
aws sts get-caller-identity
```

---

## Credenciais no Kubernetes

Secrets são injetados no cluster via `kubectl create secret`:

```powershell
kubectl create secret generic app-secrets `
    --from-env-file=.env `
    --dry-run=client -o yaml | kubectl apply -f -
```

O arquivo `.env` contém as variáveis sensíveis e está no `.gitignore`.

!!! danger "Segurança"
    Nunca commitar o arquivo `.env`, `~/.aws/credentials` ou qualquer arquivo contendo chaves AWS. Usar sempre variáveis de ambiente ou secrets do Kubernetes.

---

## VPC

A VPC foi criada via AWS CloudFormation com o template oficial para EKS:

- **Nome:** `eks-vpc`
- **Subnets públicas:** 2 (hospedam os Load Balancers)
- **Subnets privadas:** 2 (hospedam os pods do EKS)
- **Availability zones:** `us-east-2a`, `us-east-2b`
