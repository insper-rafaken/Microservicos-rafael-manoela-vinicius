# Custos & SLA

## Estimativa de custos mensais

Estimativa calculada com o [AWS Pricing Calculator](https://calculator.aws/#/estimate?id=b25e3b32f92b5b4e125abc09dd0eb1510ae3cd47) para a infraestrutura do projeto na região `us-east-2` (Ohio).

**Total estimado: $130,21 / mês**

---

## Detalhamento

| Serviço AWS | Configuração | Custo/mês |
|-------------|--------------|-----------|
| **Amazon EKS** | 1 cluster | $72,00 |
| **Amazon EC2** | 2x `t3.medium` (nodes EKS) | $30,37 |
| **Amazon EBS** | 2x volumes de 5Gi (order-db, account-db) | $1,50 |
| **Elastic Load Balancer** | 1x Application LB | $16,43 |
| **VPC / Data Transfer** | Transferência entre AZs | $9,91 |
| **Total** | | **$130,21** |

!!! note "Ambiente de desenvolvimento"
    Os custos acima refletem um ambiente de desenvolvimento/demonstração. Em produção, com alta disponibilidade e múltiplas réplicas, os custos seriam significativamente maiores.

---

## Análise de SLA

### Disponibilidade por componente

| Componente | SLA AWS | Impacto em falha |
|------------|---------|-----------------|
| EKS Control Plane | 99,95% | Indisponibilidade total |
| EC2 (nodes) | 99,99% | Redistribuição entre nodes |
| EBS (volumes) | 99,999% | Perda de dados do banco |
| ALB | 99,99% | Sem entrada de tráfego |

### SLA composto estimado

O SLA composto do sistema é o produto dos SLAs individuais dos componentes críticos:

```
SLA = 99,95% × 99,99% × 99,999% × 99,99%
    ≈ 99,93%
```

Isso equivale a aproximadamente **6,3 horas de downtime por ano**.

### Estratégias para aumentar disponibilidade

- **Multi-AZ:** Nodes do EKS distribuídos em 2 AZs (`us-east-2a`, `us-east-2b`)
- **HPA:** Escalonamento automático evita sobrecarga de pods individuais
- **PodDisruptionBudget:** Garantir mínimo de réplicas disponíveis durante manutenções
- **Health checks:** Readiness e liveness probes em todos os pods

---

## Otimizações de custo possíveis

| Otimização | Economia estimada |
|------------|------------------|
| Usar Spot Instances para nodes EKS | ~70% no EC2 |
| Reduzir para 1 node em horário de baixo tráfego | ~50% no EC2 |
| Usar EBS gp3 em vez de gp2 | ~20% no EBS |
| Reserved Instances (1 ano) | ~40% no EC2 |
