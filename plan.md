# Solo TTRPG Journal App - Kotlin Multiplatform Plan

## Problem Statement & Approach
Build a Kotlin Multiplatform text-based journaling app for solo TTRPG games with campaign and character management. Start with Desktop using Compose Multiplatform, architect for future Web/Mobile extensibility with shared business logic across all platforms.

## Tech Stack
- **UI Framework**: Compose Multiplatform (handles Desktop, Web, Mobile)
- **Database**: SQLite + SQLDelight (excellent KMM support, type-safe SQL)
- **Architecture**: Clean Architecture with shared domain/data layers
- **Build**: Gradle with KMM plugin
- **Testing**: Kotest (Kotlin native testing)

## Project Structure
```
solo-ttrpg-journal/
├── shared/                          # Common multiplatform code
│   ├── src/commonMain/kotlin/
│   │   ├── domain/                  # Business logic (pure Kotlin)
│   │   │   ├── models/              # Campaign, Character, JournalEntry
│   │   │   ├── repositories/        # Interfaces for data access
│   │   │   └── usecases/            # Business operations
│   │   └── data/
│   │       ├── local/               # SQLite/SQLDelight
│   │       └── repositories/        # Repository implementations
│   └── build.gradle.kts
├── desktopApp/                      # Desktop-specific code
│   ├── src/main/kotlin/
│   │   ├── ui/                      # Compose screens
│   │   ├── navigation/              # Screen navigation
│   │   └── Main.kt
│   └── build.gradle.kts
├── build.gradle.kts                 # Root build config
└── settings.gradle.kts
```

## MVP Features
1. **Campaign Management**: Create, view, list campaigns
2. **Character Management**: Create, view, list characters (linked to campaigns)
3. **Basic Journaling**: Write, save, view journal entries (linked to campaigns)
4. **Simple Navigation**: Tab/menu-based UI for Desktop

## Workplan

### Phase 1: Project Setup
- [ ] Create Kotlin Multiplatform project structure
- [ ] Configure Gradle with shared/desktop modules
- [ ] Set up Compose Multiplatform for Desktop
- [ ] Configure SQLDelight for database layer
- [ ] Set up basic project dependencies

### Phase 2: Domain & Data Layer (Shared)
- [ ] Define domain models: Campaign, Character, JournalEntry
- [ ] Create repository interfaces
- [ ] Set up SQLDelight schema for campaigns, characters, entries
- [ ] Implement local repository with SQLDelight
- [ ] Create basic use cases (CRUD operations)

### Phase 3: Desktop UI
- [ ] Set up Compose navigation structure
- [ ] Build campaign list/detail screens
- [ ] Build character list/detail screens
- [ ] Build journal entry list/editor screens
- [ ] Connect UI to use cases/repositories

### Phase 4: Testing & Polish
- [ ] Add unit tests for domain layer
- [ ] Add integration tests for repositories
- [ ] Polish UI/UX
- [ ] Create sample data for testing

### Phase 5: Future Extensibility
- [ ] Document shared API for Web/Mobile
- [ ] Plan Web module structure (Compose Web)
- [ ] Plan Mobile module structure (Compose Mobile)
- [ ] Create extension guide

## Extensibility Considerations
- **Shared Logic**: All business logic in `shared` module - Web/Mobile will reuse it
- **UI Separation**: Desktop UI in `desktopApp`, Web/Mobile in separate modules
- **Database**: SQLDelight works on all platforms (native SQL for each)
- **Dependency Injection**: Use Koin for cross-platform DI (easy to swap implementations)
- **API Design**: Design domain models and repositories to be platform-agnostic

## Future SRD/RuleSystem Import
- Will use same database layer - add tables for RuleSystems, Rules, Content
- Parser layer can be shared code in domain
- UI adapted per platform for browsing/searching large datasets
