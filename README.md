Android проект TaskPhotoTracker для создания заметок
========================================================================================

# TaskPhotoTracker

Todo App - это приложение, которое позволяет пользователям создавать список задач, которые нужно выполнить.
Приложение имеет архитектуру MVVM. UI представлен через Views и ViewGroups с использованием view и data binding, UI-state представлен в ViewModel через Kotlin Flows.
В качестве DI-фреймворка используется Dagger2. Для навигации используется Navigation Component.
Уведомления пользователя о текущих задачах реализовано через WorkManager.

## Основные экраны приложения

TaskPhotoTracker включает в себя следующие экраны:

### Главный экран

Главный экран содержит BottomNavigation на список задач пользователя либо список фото отчетов.

Cписок задач пользователя представлен с помощью RecyclerView с использованием DiffUtil и ShimmerLayout для отображения загрузки данных из персистентого хранилища (БД Room).

<img src="img/loading_shimmer.jpg" width="300" height="600">
<img src="img/tasks_list.jpg" width="300" height="600">

Возможно удаление нескольких задач через Contextual Action Mode.

<img src="img/action_mode.jpg" width="300" height="600">

### Экран логина

Аутентификация через VK-аккаунт, для проверки токена используется http-вызов через Retrofit. Данные о логине (идентификатор пользователя, токен и статус логина хранятся в Preferences DataStore)

<img src="img/login_page.jpg" width="300" height="600">

### Экран добавления/редактирования задачи

<img src="img/bottom_sheet_calendar.jpg" width="300" height="600">

### Экран редактирования профиля пользователя

Экран редактирования профиля пользователя позволяет отредактировать общую информацию о текущем авторизованном пользователе (выбрать фото, ввести email, имя).
Загрузка фото происходит с помощью Glide.
Информация кратко дублируется в Drawer

<img src="img/user_drawer.jpg" width="300" height="600">

### Экран фото отчетов для завершенных задач

Содержит список загруженных в Internal Storage фото

<img src="img/photo_report.jpg" width="300" height="600">
