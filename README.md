Sistema de Gerenciamento de Treinos – Kotlin & Firebase

Este projeto consiste no desenvolvimento de um aplicativo Android, escrito em Kotlin, destinado ao gerenciamento de alunos, exercícios e treinos.
A aplicação implementa operações completas de CRUD, utiliza Firebase Authentication para registro/login de usuários e Firebase Firestore como base de dados na nuvem.

# Sumário

Descrição Geral

Tecnologias Utilizadas

Arquitetura e Organização do Projeto

Modelos de Dados

Funcionalidades Implementadas

Fluxo da Aplicação

Como Executar o Projeto

Possíveis Melhorias Futuras

Licença

# Descrição Geral

O sistema foi desenvolvido para fins acadêmicos e demonstra a criação de uma aplicação completa com backend em nuvem, interface Android e arquitetura organizada em camadas.
Permite que o usuário:

Cadastre-se e efetue login.

Gerencie Alunos, Exercícios e Treinos.

Associe exercícios a treinos.

Visualize, edite ou exclua qualquer registro criado.

Todo o armazenamento é feito no Firebase Firestore, enquanto a autenticação é gerenciada via Firebase Authentication.

# Tecnologias Utilizadas

Kotlin (Android)

Firebase Authentication – gerenciamento de usuários

Firebase Realtime Database – armazenamento de dados

Material Design

Adapters personalizados (RecyclerView)

# Arquitetura e Organização do Projeto

A estrutura de diretórios está organizada para facilitar manutenção e escalabilidade:

com.example.trabalhoiii

-kotlin+java
  -activity
    -adapter
  -model
-res
  -drawable
  -layout
  -mipmap
  -values
  -xml

Resumo da Estrutura

activity/ – Contém todas as telas (Activities) da aplicação.

activity/adapter/ – Adapters utilizados nas listas (RecyclerView).

model/ – Representação das entidades da aplicação.

res/layout/ – Telas, diálogos e itens visuais.

# Modelos de Dados
# Aluno
data class Aluno(
    val id: String? = "",
    val nome: String = "",
    val idade: Int = 0
)

# Exercicio
data class Exercicio(
    val id: String? = "",
    val nome: String = "",
    val grupoMuscular: String = ""
)

# Treino
data class Treino(
    val id: String? = "",
    val nome: String = "",
    val objetivo: String = "",
    val exerciciosIds: MutableList<String> = mutableListOf()
)

Cada classe é armazenada como documento no Firestore.

# Funcionalidades Implementadas
# Autenticação

Registro de novos usuários

Login com validação pelo Firebase

Persistência de sessão

# CRUD para todas as entidades

Aluno

Cadastro, edição, remoção e listagem

Exercício

Cadastro, edição, remoção e listagem

Treino

Cadastro, edição, remoção e listagem

Associação de exercícios a treinos

# Adapters (RecyclerView)

Listagem responsiva e dinâmica de alunos, exercícios e treinos

Implementação modular para reuso

# Interface

Telas organizadas e separadas por função

Uso de diálogos para adicionar registros

Componentes Material Design

# Fluxo da Aplicação

RegisterActivity → Usuário cria conta

LoginActivity → Usuário faz login

MainActivity → Menu principal

A partir do menu:

Gerenciamento de Alunos

Gerenciamento de Exercícios

Gerenciamento de Treinos

Em Treinos, é possível:

Criar um treino

Incluir exercícios existentes

Editar ou remover o treino

# Como Executar o Projeto
1. Clone o repositório
git clone https://github.com/Tekomiw/TrabalhoIII.git

2. Abra no Android Studio
3. Configure o Firebase

Crie o projeto em: https://console.firebase.google.com/

Habilite:

Authentication (Email/Senha)

Realtime Database

Baixe e coloque o arquivo google-services.json dentro de:

app/google-services.json

4. Dependências essenciais no build.gradle
  implementation(libs.firebase.database)
  implementation(platform("com.google.firebase:firebase-bom:33.5.1"))
  implementation("com.google.firebase:firebase-auth-ktx")

6. Execute o app

Via emulador ou dispositivo físico.

# Possíveis Melhorias Futuras

Dashboard de progresso por aluno

Sistema de séries e repetições em cada exercício

Upload de imagens com Firebase Storage

Migração para arquitetura MVVM

Implementação de ViewModel + LiveData ou Flow

Dark Mode

#Licença

Este projeto pode ser utilizado livremente para fins educacionais e acadêmicos.
