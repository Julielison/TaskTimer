# Implementação do Room Database com Sincronização Firebase

## O que foi implementado

A biblioteca **Room** foi adicionada ao projeto TaskTimer como camada de persistência local, integrada com **Firebase Firestore** para sincronização em nuvem. O sistema implementa uma arquitetura **"local-first"** (offline-first).

## Arquitetura de Sincronização

### Estratégia "Local-First" com Sincronização Bidirecional
1. **Todas as operações de escrita são executadas primeiro no Room** (banco de dados local)
2. **Após sucesso local, sincroniza com Firebase** em background (Room → Firebase)
3. **Monitora mudanças do Firebase em tempo real** e atualiza Room automaticamente (Firebase → Room)
4. **Falhas de sincronização não afetam a operação local** - o app continua funcionando offline
5. **Dados locais são a fonte da verdade** - garantindo funcionamento offline completo

### Fluxo de Operações

#### Escrita (Room → Firebase)
```
Usuário → RoomRepository → Room (local) ✓ → Firebase (nuvem) [background]
                              ↓
                         Retorna sucesso imediatamente
```

#### Leitura Contínua (Firebase → Room)
```
Firebase (mudanças) → RoomRepository (listener) → Room (atualiza local)
                                                      ↓
                                                 UI atualiza automaticamente
```

### Sincronização Bidirecional

#### Automática (Contínua)
- **Inicia automaticamente** quando o `RoomRepository` é criado
- **Monitora mudanças** do Firebase em tempo real usando `Flow`
- **Detecta novas tasks/categorias** e adiciona ao Room
- **Executa em background** sem bloquear a UI

#### Manual (Sob Demanda)
```kotlin
// Sincronizar todos os dados do Firebase para Room
roomRepository.syncAllFromFirebase()

// Pausar sincronização automática (economizar recursos)
roomRepository.stopFirebaseSync()

// Retomar sincronização automática
roomRepository.resumeFirebaseSync()
```

### Benefícios
- ✅ **Funcionamento offline**: App funciona sem conexão de internet
- ✅ **Performance**: Operações locais são instantâneas
- ✅ **Resiliência**: Falhas de rede não afetam o usuário
- ✅ **Sincronização automática bidirecional**: 
  - Room → Firebase: Mudanças locais são enviadas para a nuvem
  - Firebase → Room: Mudanças de outros dispositivos são recebidas automaticamente
- ✅ **Backup na nuvem**: Dados persistem no Firebase para múltiplos dispositivos
- ✅ **Multi-dispositivo**: Dados sincronizam entre diferentes dispositivos
- ✅ **Recuperação de desastres**: Dados podem ser restaurados do Firebase

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

#### `RoomRepository` ⭐ **NOVO: Sincronização Firebase**
- **Repositório unificado** com integração Room + Firebase
- Implementa estratégia "local-first"
- **Operações locais**: Executa primeiro no Room
- **Sincronização automática**: Propaga mudanças para Firebase em background
- **Tratamento de erros**: Falhas de sincronização são logadas mas não bloqueiam
- **Coroutines**: Usa CoroutineScope separado para sincronização assíncrona

#### `FirestoreRepository`
- Mantido para sincronização com Firebase
- Usado internamente pelo `RoomRepository`
- Não deve ser usado diretamente pelos ViewModels

### 7. **Injeção de Dependência** (`AppModule.kt`)
Configuração atualizada com sincronização Firebase:
```kotlin
// Database e DAOs
single { AppDatabase.getDatabase(androidContext()) }
single { get<AppDatabase>().taskDao() }
single { get<AppDatabase>().categoryDao() }

// Repositórios Room (camada local)
single { RoomCategoryRepository(get()) }
single { RoomTaskRepository(get(), get()) }
single { RoomFocusStatsRepository(get(), get()) }

// Firebase Repository (para sincronização)
single { FirestoreRepository() }

// Repositório unificado com sincronização Room + Firebase
single { RoomRepository(get(), get(), get(), get<FirestoreRepository>()) }

// ViewModels usam RoomRepository (que sincroniza automaticamente)
viewModel { HomeViewModel(get<RoomRepository>(), get()) }
viewModel { CalendarViewModel(get<RoomRepository>()) }
// ... outros ViewModels
```

## Benefícios da Implementação

### Room (Local)
1. **Offline First**: Dados persistem localmente e app funciona sem internet
2. **Performance**: Acesso instantâneo aos dados sem latência de rede
3. **Type Safety**: Verificação em tempo de compilação
4. **Observabilidade**: Suporte nativo a Flow para observar mudanças
5. **Relações**: Suporte a Foreign Keys e relacionamentos
6. **Migrações**: Sistema robusto de versionamento de schema

### Firebase (Nuvem)
1. **Backup automático**: Dados salvos na nuvem
2. **Sincronização multi-dispositivo**: Dados acessíveis em múltiplos dispositivos
3. **Escalabilidade**: Infraestrutura gerenciada pelo Google
4. **Colaboração**: Suporte a compartilhamento de dados entre usuários

### Integração Room + Firebase
1. **Melhor de dois mundos**: Performance local + backup em nuvem
2. **Resiliência**: App continua funcionando mesmo sem internet
3. **Sincronização transparente**: Usuário não percebe a sincronização
4. **Recuperação de desastres**: Dados preservados na nuvem
5. **Flexibilidade**: Fácil adicionar sincronização bidirecional no futuro

## Como usar

Todas as operações através do `RoomRepository` são automaticamente sincronizadas com Firebase:

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
// ✓ Task salva no Room imediatamente
// ✓ Task enviada ao Firebase em background
// ✓ Outros dispositivos receberão a task automaticamente
```

### Observar tasks (sempre do Room)
```kotlin
roomRepository.getTasksFlow().collect { tasks ->
    // Atualiza UI automaticamente quando dados locais mudam
    // Dados sempre vêm do Room (rápido e offline)
    // Mudanças do Firebase são refletidas automaticamente
}
```

### Buscar tasks por data
```kotlin
val tasks = roomRepository.getTasksByDate(LocalDate.now())
// Retorna dados do Room instantaneamente
```

### Atualizar task
```kotlin
roomRepository.updateTask(taskId, title, description, ...)
// ✓ Atualização local imediata
// ✓ Sincronização com Firebase em background
```

### Deletar task
```kotlin
roomRepository.deleteTask(taskId)
// ✓ Remoção local imediata
// ✓ Remoção no Firebase em background
```

### Sincronização manual (opcional)
```kotlin
// Sincronizar todos os dados do Firebase para o Room
// Útil após login ou para recuperar dados
roomRepository.syncAllFromFirebase()

// Pausar sincronização automática (economizar bateria/dados)
roomRepository.stopFirebaseSync()

// Retomar sincronização automática
roomRepository.resumeFirebaseSync()
```

## Logs de Sincronização

O sistema loga automaticamente todas as operações de sincronização:

### Sincronização bem-sucedida (Room → Firebase)
```
D/RoomRepository: Task sincronizada do Firebase: <taskId>
D/RoomRepository: Categoria sincronizada do Firebase: <categoryId>
```

### Falhas de sincronização
```
W/RoomRepository: Falha na sincronização Room → Firebase: <erro>
W/RoomRepository: Erro ao sincronizar tasks Firebase → Room: <erro>
```

### Sincronização completa
```
D/RoomRepository: Iniciando sincronização completa Firebase → Room
D/RoomRepository: Sincronização completa finalizada: X categorias, Y tasks
```

**Importante:** Logs de erro são apenas informativos - a operação local sempre é concluída com sucesso.

## Próximos passos (opcional)

### Melhorias Implementadas ✅
1. ✅ **Sincronização bidirecional**: Firebase ↔ Room
   - ✅ Room → Firebase (escrita)
   - ✅ Firebase → Room (leitura automática)
   - ✅ Listeners em tempo real
   
2. ✅ **Sincronização automática**:
   - ✅ Inicia automaticamente no init
   - ✅ Monitora mudanças do Firebase
   - ✅ Métodos para pausar/retomar

### Melhorias Futuras Possíveis
1. **Resolução de conflitos**:
   - Detectar conflitos quando mesma task é modificada em dois dispositivos
   - Estratégias: última modificação ganha, merge manual, etc.
   - Timestamps de modificação para detectar conflitos
   
2. **Estratégia de sincronização avançada**:
   - Fila de sincronização persistente (WorkManager)
   - Retry automático com backoff exponencial
   - Indicador de status de sincronização na UI
   - Badge mostrando itens pendentes de sync
   
3. **Otimizações**:
   - Batch de operações para reduzir chamadas Firebase
   - Sincronização incremental (apenas dados modificados)
   - Compressão de dados para economizar bandwidth
   - Delta sync (apenas diferenças)

4. **Autenticação**:
   - Integrar Firebase Authentication
   - Sincronizar apenas dados do usuário logado
   - Suporte a compartilhamento de tasks entre usuários
   - Permissões por categoria/task

5. **Monitoramento avançado**:
   - Métricas de sincronização (sucesso/falha)
   - Dashboard de status da sincronização
   - Alertas para falhas persistentes
   - Analytics de uso offline vs online

## Compatibilidade

O código foi estruturado para facilitar futuras melhorias:
- `RoomRepository` é a interface única para acesso a dados
- ViewModels não conhecem a implementação interna (Room + Firebase)
- Fácil adicionar camada de sincronização mais sofisticada
- `FirestoreRepository` pode ser estendido para sincronização bidirecional

## Monitoramento

### Logs de Sincronização
Para monitorar a sincronização em tempo real:

1. **Android Studio Logcat**: Filtre por tag `RoomRepository`
   ```
   adb logcat -s RoomRepository
   ```

2. **Níveis de log**:
   - `D` (Debug): Operações bem-sucedidas
   - `W` (Warning): Falhas não críticas (sincronização)
   - `E` (Error): Erros críticos

### Testar Sincronização

#### Teste offline/online
```kotlin
// 1. Desabilitar rede
// 2. Adicionar tasks localmente
// 3. Verificar que tasks foram salvas no Room
// 4. Habilitar rede
// 5. Verificar logs - tasks devem ser enviadas ao Firebase
```

#### Teste multi-dispositivo
```kotlin
// 1. Abrir app em dois dispositivos
// 2. Adicionar task no dispositivo A
// 3. Aguardar alguns segundos
// 4. Verificar que task aparece no dispositivo B
```

#### Sincronização manual
```kotlin
// No ViewModel ou Activity
lifecycleScope.launch {
    roomRepository.syncAllFromFirebase()
    // Verificar logs para confirmar sincronização
}
```

### Performance
- Use Android Studio Profiler para observar:
  - Chamadas de rede (Firebase)
  - Queries do Room
  - Uso de CPU/memória pelos listeners

### Depuração
```kotlin
// Para debug detalhado, adicione logs personalizados:
class RoomRepository(...) {
    init {
        Log.d(TAG, "Iniciando sincronização automática")
        startFirebaseSync()
    }
}
```
