# PaaS — Platform as a Service

## O que é PaaS

PaaS (Platform as a Service) é um modelo de computação em nuvem que fornece uma plataforma para desenvolver, executar e gerenciar aplicações sem a complexidade de construir e manter a infraestrutura subjacente.

```
┌────────────────────────────────┐
│           SaaS                 │  Software como serviço
├────────────────────────────────┤
│           PaaS                 │  Plataforma como serviço  ← nosso foco
├────────────────────────────────┤
│           IaaS                 │  Infraestrutura como serviço
├────────────────────────────────┤
│      On-premises               │  Infraestrutura própria
└────────────────────────────────┘
```

---

## Uso de PaaS no projeto

### Amazon EKS (Elastic Kubernetes Service)

O principal uso de PaaS no projeto é o **Amazon EKS**. Em vez de instalar e operar o control plane do Kubernetes manualmente em VMs (o que seria IaaS), usamos o EKS que:

- **Gerencia o control plane automaticamente** — API server, etcd, scheduler, controller manager
- **Oferece alta disponibilidade** com SLA de 99,95%
- **Integra-se nativamente** com outros serviços AWS (IAM, EBS, ALB, VPC)
- **Atualiza automaticamente** os componentes do control plane

O grupo é responsável apenas pelos **worker nodes** e pelos **workloads** (pods). Toda a complexidade operacional do Kubernetes é abstraída.

### Amazon RDS (potencial)

O projeto usa PostgreSQL em containers no EKS. Uma alternativa PaaS seria o **Amazon RDS**, que gerenciaria backups, patches e réplicas automaticamente. A escolha por containers foi motivada pelo controle e custo reduzido para o ambiente de desenvolvimento.

---

## Comparativo IaaS vs PaaS no contexto do projeto

| Aspecto | IaaS (EC2 + k8s manual) | PaaS (EKS) |
|---------|------------------------|------------|
| Setup do control plane | Manual (~8h) | Automático (console) |
| Atualizações do k8s | Manual e arriscado | Gerenciado pela AWS |
| Alta disponibilidade | Configuração manual | Incluída no SLA |
| Custo operacional | Alto (tempo de equipe) | Baixo (pago na fatura) |
| Custo financeiro | Menor | Maior ($72/mês pelo cluster) |
| Flexibilidade | Total | Limitada pela AWS |

Para um projeto universitário e para produção com equipes pequenas, o PaaS (EKS) representa o melhor trade-off: reduz drasticamente o esforço operacional ao custo de uma taxa de gerenciamento.

---

## Outros serviços gerenciados utilizados

| Serviço | Categoria | O que abstrai |
|---------|-----------|---------------|
| Amazon EKS | PaaS | Gerenciamento do control plane Kubernetes |
| Amazon EBS (via CSI) | IaaS/PaaS | Provisionamento automático de volumes |
| Application Load Balancer | PaaS | Balanceamento de carga, SSL termination |
| Amazon VPC | IaaS | Rede virtual isolada |
| AWS IAM | PaaS | Gerenciamento de identidade e acesso |
