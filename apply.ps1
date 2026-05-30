# apply.ps1 — Deploy completo no EKS
# Uso: .\apply.ps1
# Pre-requisito: kubectl configurado com o contexto do cluster EKS

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function Apply {
    param([string]$Label, [string]$File)
    Write-Host "`n[$Label] Aplicando $File ..."
    kubectl apply -f $File
    if ($LASTEXITCODE -ne 0) { throw "Falha ao aplicar $File" }
}

# ── 1. Secret a partir do .env ───────────────────────────────────────────────
Write-Host "`n[Secret] Criando app-secrets a partir do .env ..."
kubectl create secret generic app-secrets `
    --from-env-file=.env `
    --dry-run=client -o yaml | kubectl apply -f -
if ($LASTEXITCODE -ne 0) { throw "Falha ao criar app-secrets" }

# ── 2. StorageClass (deve existir antes dos PVCs) ────────────────────────────
Apply "StorageClass" "k8s/storageclass.yaml"

# ── 3. Infraestrutura de suporte ─────────────────────────────────────────────
Apply "RabbitMQ" "k8s/rabbitmq.yaml"
Apply "Redis"    "k8s/redis.yaml"

# ── 4. Serviços de aplicação (ordem respeita depends_on) ─────────────────────
Apply "Account"  "api/account/account-service/k8s/k8s.yaml"
Apply "Auth"     "api/auth/auth-service/k8s/k8s.yaml"
Apply "Order"    "api/order/order-service/k8s/k8s.yaml"
Apply "Gateway"  "api/gateway/gateway-service/k8s/k8s.yaml"

# ── 5. Verificação rápida ────────────────────────────────────────────────────
Write-Host "`n[OK] Todos os recursos aplicados. Estado atual:"
kubectl get deployments,services,pvc
