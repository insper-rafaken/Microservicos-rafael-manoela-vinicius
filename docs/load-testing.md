# Load Testing

Testes de carga realizados no cluster EKS com HPA (Horizontal Pod Autoscaler) no Gateway, demonstrando o escalonamento automático de pods sob alta demanda.

---

## HPA — Horizontal Pod Autoscaler

O HPA monitora o consumo de CPU dos pods do Gateway e escala automaticamente o número de réplicas:

```bash
kubectl autoscale deployment gateway --cpu-percent=50 --min=1 --max=10
```

| Parâmetro | Valor |
|-----------|-------|
| CPU target | 50% |
| Réplicas mínimas | 1 |
| Réplicas máximas | 10 |

---

## Executando o teste de carga

### 1. Verificar o HPA

```bash
kubectl get hpa
```

Saída esperada inicial:
```
NAME      REFERENCE            TARGETS   MINPODS   MAXPODS   REPLICAS
gateway   Deployment/gateway   5%/50%    1         10        1
```

### 2. Iniciar o gerador de carga

Em um terminal, rodar o gerador de carga contínuo via busybox:

```bash
kubectl run -i --tty load-generator \
    --rm \
    --image=busybox:1.28 \
    --restart=Never \
    -- /bin/sh -c "while sleep 0.01; do wget -q -O- http://gateway/health-check; done"
```

### 3. Monitorar o escalonamento

Em outro terminal, monitorar o HPA e os pods em tempo real:

```bash
# monitorar HPA
kubectl get hpa -w

# monitorar pods
kubectl get pods -l app=gateway -w
```

### 4. Observar o escalonamento

Após alguns minutos sob carga, o HPA detecta que o uso de CPU ultrapassou 50% e cria novos pods:

```
NAME      REFERENCE            TARGETS    MINPODS   MAXPODS   REPLICAS
gateway   Deployment/gateway   23%/50%    1         10        9
```

```
NAME                       READY   STATUS    AGE
gateway-79d4dddb79-9cn8f   1/1     Running   10m
gateway-79d4dddb79-kx2lp   1/1     Running   45s
gateway-79d4dddb79-mw9rt   1/1     Running   45s
gateway-79d4dddb79-p4qr8   1/1     Running   43s
gateway-79d4dddb79-t7nxv   1/1     Running   43s
gateway-79d4dddb79-w2zjd   1/1     Running   42s
gateway-79d4dddb79-x8bln   1/1     Running   42s
gateway-79d4dddb79-yc3ms   1/1     Running   41s
gateway-79d4dddb79-zd9fk   1/1     Running   41s
```

### 5. Encerrar o teste

Interromper o gerador de carga (Ctrl+C) e aguardar o scale-down. Remover o HPA ao final:

```bash
kubectl delete hpa gateway
```

---

## Resultado

O HPA demonstrou escalonamento automático efetivo:

- **Início:** 1 réplica do Gateway, CPU < 10%
- **Sob carga:** CPU chegou a ~23%, HPA escalonou até 9 réplicas em menos de 2 minutos
- **Após carga:** Scale-down automático de volta a 1 réplica

O escalonamento horizontal garante que a plataforma suporte picos de tráfego sem intervenção manual.

---

## Vídeo

🎥 Vídeo do teste de carga será adicionado aqui
