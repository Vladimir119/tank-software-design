# Ports and adapters

| Слой | Классы |
| --- | --- |
| Domain | Tank, Bullet, Tree, Field, Cell, Direction, GameObject, GameWorld, TankState и его реализации, TankStates, LevelData, TankSpawner |
| Application | GameSession, Command и команды, HealthDisplay, порты GameView, ControlStrategy, LevelSource |
| Infrastructure | GameDesktopLauncher, LaunchOptions, KeyboardController, RandomController, реализации LevelSource, TextLevelParser, SceneObjects, рендереры и util |

Порты приложения находятся в `application.port`. Ввод реализует ControlStrategy,
графика — GameView, источники уровней — LevelSource. SceneObjects принимает
события через WorldListener. MovementPolicy и WorldAccess — внутренние порты
домена; GameWorld реализует их как доменный сервис, а не внешний адаптер.
TankState — интерфейс доменного поведения, не порт внешней системы.

Раньше GameSession импортировал GameView из graphics, а модели и команда
стрельбы зависели от конкретного GameWorld. Порты вынесены внутрь приложения,
а доступ к миру отделён интерфейсом WorldAccess. Файловые операции и LibGDX
остались в адаптерах. Создание конкретных реализаций остаётся в launcher.

Для сохранения прогресса понадобится выходной порт GameSave и файловый адаптер.
Для внешних рекомендаций AI — порт выбора команд и адаптер рекомендательного
сервиса. Этих функций сейчас нет, поэтому пустые интерфейсы для них не добавлены.

Состояние выбирается при получении урона: больше 70% — лёгкое, от 15% до 70%
включительно — среднее, меньше 15% — тяжёлое. Смена состояния меняет скорость
оставшейся части текущего шага. Новое поведение можно реализовать через TankState
и назначить методом changeState; скорость движения и перезарядки задаются отдельно.
