# Tanks

Нужен JDK 17. Запуск из корня проекта:

```shell
./gradlew run
```

Windows: `gradlew.bat run`. В IDE — Gradle → application → run.
Управление: стрелки или WASD. Деревья и границы поля блокируют движение.

## Задание 1

Выделены Tank, Tree, Field и Direction. Проверка движения общая для всех
направлений, состояние танка хранится отдельно от его текстуры.
