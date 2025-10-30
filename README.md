# 7Even — Магазин продуктов

<p align="center">
  <img src="docs/screenshots/catalog_light.png" alt="Каталог (light)" width="220"/>
  <img src="docs/screenshots/detail_dark.png" alt="Детали (dark)" width="220"/>
  <img src="docs/screenshots/cart_light.png" alt="Корзина (light)" width="220"/>
  <img src="docs/screenshots/settings_dark.png" alt="Настройки (dark)" width="220"/>
</p>

Android‑приложение на Kotlin и Jetpack Compose с каталогом, поиском и адаптацией под тёмную тему.

## Основные возможности

- **Каталог продуктов**: сетка товаров, категории, скидки, быстрые действия
- **Умный поиск**: Unicode‑поиск (русский ввод), дебаунс, релевантная сортировка
- **Новая строка поиска**: DockedSearchBar без «подбородка», анимации появления/скрытия, очистка, последние запросы
- **Детали товара**: фото с кроссфейдом, скидка‑бейдж, шаринг карточки
- **Корзина**: счётчик в тулбаре, управление количеством, подсчёт итогов
- **Избранное**: экран избранных товаров, добавление из карточки
- **Акции**: отдельный экран «Акции», бейдж количества в меню
- **Настройки**: выбор темы (Системная / Светлая / Тёмная) с мини‑превью
- **Тёмная тема**: улучшенная палитра M3, onSurface/Variant, error‑контейнеры
- **Навигация**: компактное overflow‑меню (сортировка, акции, избранное, настройки)
- **Полировка UI/UX**: хаптика и микропружины на действия, шимер‑плейсхолдеры

## Технологии

- **Kotlin** - Основной язык программирования
- **Jetpack Compose** - Современный UI toolkit
- **Material Design 3** - Дизайн система
- **Navigation Compose** - Навигация между экранами
- **ViewModel** - Управление состоянием
- **StateFlow** - Реактивные потоки данных
- **Coil** - Загрузка изображений
- **Accompanist Placeholder** - Шимер‑плейсхолдеры (легаси)
- **Material 3** - Тема/палитра, SearchBar, BadgedBox, Overflow menu

## Экраны приложения

### 1. Каталог
- Сетка товаров с изображениями и скидками
- DockedSearchBar с русской раскладкой и анимациями
- Сортировка (имя / цена / скидка) — в overflow‑меню
- Бейдж корзины, избранное из карточки, плавные анимации

Скриншоты:

<p>
  <img src="docs/screenshots/catalog_light.png" alt="Каталог (светлая тема)" width="320"/>
  <img src="docs/screenshots/catalog_dark.png" alt="Каталог (тёмная тема)" width="320"/>
</p>

### 2. Детали товара
- Большое фото (Coil, crossfade), шимер при загрузке
- Бейдж скидки, описание, выбор количества
- Кнопка «Поделиться» (стандартный системный диалог)

Скриншоты:

<p>
  <img src="docs/screenshots/detail_light.png" alt="Детали товара (светлая тема)" width="320"/>
  <img src="docs/screenshots/detail_dark.png" alt="Детали товара (тёмная тема)" width="320"/>
</p>

### 3. Корзина
- Список товаров, управление количеством
- Итоговая стоимость, быстрый переход из тулбара

Скриншоты:

<p>
  <img src="docs/screenshots/cart_light.png" alt="Корзина (светлая тема)" width="320"/>
  <img src="docs/screenshots/cart_dark.png" alt="Корзина (тёмная тема)" width="320"/>
</p>

### 4. Избранное
- Список избранных товаров, очистка

Скриншоты:

<p>
  <img src="docs/screenshots/favorites_light.png" alt="Избранное (светлая тема)" width="320"/>
  <img src="docs/screenshots/favorites_dark.png" alt="Избранное (тёмная тема)" width="320"/>
</p>

### 5. Акции
- Все товары со скидками, сортировка по размеру скидки

Скриншоты:

<p>
  <img src="docs/screenshots/deals_light.png" alt="Акции (светлая тема)" width="320"/>
  <img src="docs/screenshots/deals_dark.png" alt="Акции (тёмная тема)" width="320"/>
</p>

### 6. Настройки
- Переключение темы (Системная / Светлая / Тёмная) + мини‑превью

Скриншоты:

<p>
  <img src="docs/screenshots/settings_light.png" alt="Настройки (светлая тема)" width="320"/>
  <img src="docs/screenshots/settings_dark.png" alt="Настройки (тёмная тема)" width="320"/>
</p>

## Дизайн

Material Design 3 + кастомные схемы для светлой/тёмной темы:
- Улучшенные `onBackground`, `onSurface`, `surfaceVariant`, `onSurfaceVariant`
- Контейнеры ошибок `errorContainer`, `onErrorContainer`

## Структура проекта

```
app/src/main/java/com/example/product_store/
├── data/
│   ├── Product.kt                 # Модель продукта
│   └── CartItem.kt                # Модель корзины
├── navigation/
│   └── ProductStoreNavigation.kt  # Навигация
├── ui/
│   ├── screens/
│   │   ├── ProductCatalogScreen.kt    # Каталог
│   │   ├── ProductDetailScreen.kt     # Детали
│   │   ├── CartScreen.kt              # Корзина
│   │   ├── FavoritesScreen.kt         # Избранное
│   │   ├── SettingsScreen.kt          # Настройки (тема с превью)
│   │   └── DealsScreen.kt             # Акции
│   └── theme/
│       ├── Color.kt                   # Цвета
│       ├── Theme.kt                   # Тема
│       └── Type.kt                    # Типографика
├── viewmodel/
│   └── ProductStoreViewModel.kt      # ViewModel
└── MainActivity.kt                    # Главная активность
```

## Запуск

1. Откройте проект в Android Studio
2. Sync Gradle
3. Запустите на эмуляторе/устройстве из IDE
   или из консоли:

   ```bash
   ./gradlew installDebug
   ```

## Требования

- Android API 35+
- Kotlin 2.0.21+
- Jetpack Compose
- Android Studio Arctic Fox или новее
---