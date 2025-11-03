# Testes da API de Pessoas

## Visão Geral

Este documento descreve os testes implementados para a API de Pessoas (`PessoaResource`).

## Estrutura dos Testes

### Arquivos de Teste

1. **PessoaResourceTest.java** - Testes unitários para execução em modo JVM
2. **PessoaResourceIT.java** - Testes de integração para execução em modo nativo

## Cobertura de Testes

### 1. `testGetAllPessoas`
**Objetivo:** Verificar se o endpoint GET `/api/pessoa` retorna todas as pessoas cadastradas.
- Valida status code 200
- Verifica que retorna 7 pessoas (dados iniciais do import.sql)
- Confirma que os nomes esperados estão presentes

### 2. `testFindByAnoNascimento`
**Objetivo:** Testar a busca de pessoas por ano de nascimento.
- Busca por pessoas nascidas em 1970
- Valida que retorna exatamente 1 pessoa (Leonardo)
- Confirma os dados da pessoa retornada

### 3. `testFindByAnoNascimentoMultipleResults`
**Objetivo:** Testar busca que retorna múltiplos resultados.
- Busca por ano de nascimento 1996
- Valida que retorna 1 resultado

### 4. `testFindByAnoNascimentoNotFound`
**Objetivo:** Testar busca que não encontra resultados.
- Busca por ano de nascimento 2000 (não existe)
- Valida que retorna uma lista vazia

### 5. `testCreatePessoa`
**Objetivo:** Testar a criação de uma nova pessoa.
- Envia POST com dados válidos
- Valida status code 200
- Confirma que o ID foi gerado automaticamente
- Verifica os dados da pessoa criada

### 6. `testCreatePessoaWithId`
**Objetivo:** Verificar que o ID fornecido no POST é ignorado.
- Envia POST com ID=999
- Valida que um novo ID é gerado (diferente de 999)
- Confirma que os demais dados são persistidos corretamente

### 7. `testUpdatePessoa`
**Objetivo:** Testar a atualização de uma pessoa existente.
- Cria uma pessoa
- Atualiza os dados dela via PUT
- Verifica status code 200
- Confirma que os dados foram atualizados
- Faz uma consulta GET para validar a persistência

### 8. `testDeletePessoa`
**Objetivo:** Testar a exclusão de uma pessoa.
- Cria uma pessoa
- Deleta via DELETE `/api/pessoa/{id}`
- Valida status code 204
- Confirma que a pessoa não existe mais na listagem

### 9. `testDeleteNonExistingPessoa`
**Objetivo:** Testar exclusão de pessoa inexistente.
- Tenta deletar ID 99999
- Valida status code 204 (Panache não retorna erro)

### 10. `testCreatePessoaWithInvalidData`
**Objetivo:** Testar criação com dados inválidos.
- Envia JSON vazio
- Valida que aceita (sem validações implementadas)

### 11. `testGetPessoasAfterOperations`
**Objetivo:** Verificar o estado final após todas as operações.
- Lista todas as pessoas
- Valida que existem mais que as 7 iniciais

## Executando os Testes

### Testes Unitários (JVM Mode)
```bash
./mvnw test
```

### Testes de Integração (Native Mode)
```bash
./mvnw verify -Pnative
```

### Executar apenas os testes de PessoaResource
```bash
./mvnw test -Dtest=PessoaResourceTest
```

### Executar com log reduzido
```bash
./mvnw test -Dquarkus.log.level=ERROR
```

## Tecnologias Utilizadas

- **JUnit 5** - Framework de testes
- **REST Assured** - Testes de API REST
- **Hamcrest** - Matchers para assertions
- **Quarkus Test** - Suporte para testes em Quarkus
- **Testcontainers** - Container PostgreSQL para testes

## Configuração

Os testes utilizam:
- **PostgreSQL via Testcontainers** - Banco de dados isolado para cada execução
- **Dados iniciais** - Carregados via `import.sql`
- **Transações** - Cada teste roda em uma transação isolada
- **Ordenação** - Testes executam em ordem específica via `@Order`

## Melhorias Futuras

1. Adicionar testes de validação de dados
2. Implementar testes de performance
3. Adicionar testes de concorrência
4. Implementar testes de segurança/autenticação
5. Adicionar testes de paginação
6. Implementar testes parametrizados

## Observações

- Os endpoints estão no path `/api/pessoa` (devido ao `@ApplicationPath("api")`)
- O método DELETE foi corrigido para usar `@PathParam` em vez de parâmetro no corpo
- Os testes usam text blocks (Java 15+) para JSON strings
- Warnings sobre Java 24 e módulos podem ser ignorados (relacionados ao runtime)
