# Implementação do Room Database

## O que foi implementado

A biblioteca **Room** foi adicionada ao projeto TaskTimer como camada de persistência local, substituindo o Firestore como fonte de dados principal.

## Estrutura criada

### 1. **Dependências** (`build.gradle.kts`)
- `androidx.room:room-runtime`
- `androidx.room:room-ktx` (para suporte a Coroutines e Flow)
- `androidx.room:room-compiler` (processador KSP)
- `com.google.code.gson:gson` (para serialização de objetos complexos)

### 2. **Type Converters** (`Converters.kt`)
Classe responsável por converter tipos complexos para tipos que o Room pode armazenar:
- `LocalDateTime` ↔ `Long` (timestamp)
- `Color` ↔ `Long`
- `List<Subtask>` ↔ `String` (JSON)
- `List<PomodoroSession>` ↔ `String` (JSON)
- `PomodoroConfig` ↔ `String` (JSON)
- `PomodoroType` ↔ `String` (enum)

### 3. **Entidades** (`entity/`)
#### `TaskEntity`
- Representa uma tarefa no banco de dados
- Inclui relacionamento com `CategoryEntity` via Foreign Key
- Índices em: `categoryId`, `dateTime`, `isCompleted`

#### `CategoryEntity`
- Representa uma categoria no banco de dados
- Armazena nome, cor, ícone e timestamp de criação

### 4. **DAOs** (`dao/`)
#### `TaskDao`
Operações disponíveis:
- `getAllFlow()`: Observa todas as tasks
- `getByDateRange()`: Busca tasks por intervalo de datas
- `getCompletedByDateRange()`: Busca tasks completadas por intervalo
- `insert()`, `update()`, `delete()`: Operações CRUD
- `searchTasks()`: Busca por título ou descrição
- `removeCategoryFromTasks()`: Remove categoria das tasks

#### `CategoryDao`
Operações disponíveis:
- `getAllFlow()`: Observa todas as categorias
- `insert()`, `update()`, `delete()`: Operações CRUD
- `getById()`: Busca categoria por ID

### 5. **Database** (`AppDatabase.kt`)
- Configuração do banco de dados Room
- Define versão atual: `1`
- Usa singleton pattern para instância única
- Configurado com `fallbackToDestructiveMigration()` para desenvolvimento

### 6. **Repositórios**
#### `RoomTaskRepository`
- Gerencia operações de tasks com Room
- Converte entre `Task` (modelo) e `TaskEntity` (entidade Room)
- Gera IDs únicos usando UUID

#### `RoomCategoryRepository`
- Gerencia operações de categorias com Room
- Converte entre `Category` e `CategoryEntity`

#### `RoomFocusStatsRepository`
- Implementa `FocusStatsRepository`
- Calcula estatísticas baseadas em dados do Room
- Lógica idêntica à implementação do Firebase

#### `RoomRepository`
- **Repositório unificado** que serve como ponte
- Mantém mesma interface que `FirestoreRepository`
- Facilita migração gradual do código existente

### 7. **Injeção de Dependência** (`AppModule.kt`)
Configuração atualizada:
```kotlin
// Database e DAOs
single { AppDatabase.getDatabase(androidContext()) }
single { get<AppDatabase>().taskDao() }
single { get<AppDatabase>().categoryDao() }

// Repositórios Room
single { RoomCategoryRepository(get()) }
single { RoomTaskRepository(get(), get()) }
single { RoomRepository(get(), get()) }

// Stats usando Room
single<FocusStatsRepository> { RoomFocusStatsRepository(get(), get()) }

// ViewModels agora usam RoomRepository
viewModel { HomeViewModel(get<RoomRepository>(), get()) }
```

## Benefícios do Room

1. **Offline First**: Dados persistem localmente
2. **Performance**: Acesso rápido aos dados sem latência de rede
3. **Type Safety**: Verificação em tempo de compilação
4. **Observabilidade**: Suporte nativo a Flow para observar mudanças
5. **Relações**: Suporte a Foreign Keys e relacionamentos
6. **Migrações**: Sistema robusto de versionamento de schema

## Como usar

### Adicionar uma task
```kotlin
val taskId = roomRepository.addTask(
    title = "Minha tarefa",
    description = "Descrição",
    dateTime = LocalDateTime.now(),
    categoryId = null,
    subtasks = emptyList(),
    pomodoroConfig = null
)
```

### Observar tasks
```kotlin
roomRepository.getTasksFlow().collect { tasks ->
    // Atualiza UI automaticamente quando dados mudam
}
```

### Buscar tasks por data
```kotlin
val tasks = roomRepository.getTasksByDate(LocalDate.now())
```

## Próximos passos (opcional)

1. **Migração de dados**: Criar script para migrar dados do Firestore para Room
2. **Sincronização**: Implementar sync entre Room (local) e Firestore (nuvem)
3. **Backup**: Adicionar exportação/importação de dados
4. **Migrações**: Planejar estratégia para futuras mudanças no schema

## Compatibilidade

O código foi estruturado para manter compatibilidade com o código existente:
- `RoomRepository` mantém mesma interface que `FirestoreRepository`
- ViewModels foram atualizados minimamente
- FirestoreRepository ainda disponível se necessário para migração gradual
