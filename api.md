# GlowClans API

[Русский](#русский) | [English](#english)

---

## Русский

### Подключение

Maven (через JitPack):

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.Agent1k-1</groupId>
        <artifactId>GlowClans</artifactId>
        <version>1.3</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

Gradle:

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    compileOnly 'com.github.Agent1k-1:GlowClans:1.3'
}
```

В `plugin.yml` вашего плагина:

```yaml
depend: [ GlowClans ]
# или, если клановая интеграция необязательна:
softdepend: [ GlowClans ]
```

Версия `1.3` — это git-тег репозитория.

### Получение API

```java
GlowClansAPI api = GlowClansAPI.getInstance();
```

Если GlowClans не включён, будет выброшен `IllegalStateException`.

### Методы GlowClansAPI

| Метод | Описание |
|---|---|
| `hasClan(UUID player)` | Состоит ли игрок в клане |
| `clanExists(String tag)` | Существует ли клан с таким тегом |
| `getClanByPlayer(UUID player)` | `Optional<Clan>` клана игрока |
| `getClanByTag(String tag)` | `Optional<Clan>` по тегу |
| `getClans()` | Список всех кланов |
| `createClan(String tag, UUID owner)` | Создаёт клан, возвращает `CompletableFuture<Clan>` |

`createClan` не списывает деньги и не проверяет ограничения из `config.yml` (длина тега, запрещённые названия). Он проверяет только: тег от 1 до 64 символов без пробелов, клан с таким тегом не существует, владелец не состоит в другом клане. При нарушении выбрасывается `IllegalArgumentException` или `IllegalStateException`.

### Методы Clan

`Clan` — это «живая» ссылка на клан: данные читаются из кэша плагина при каждом вызове.

Чтение:

| Метод | Описание |
|---|---|
| `getTag()` | Тег клана |
| `exists()` | Существует ли клан до сих пор |
| `getOwner()` | UUID владельца |
| `getMembers()` | UUID всех участников (включая владельца) |
| `getMemberCount()` | Количество участников |
| `isMember(UUID)` / `isOwner(UUID)` | Проверка участника / владельца |
| `getKills()` | Убийства клана |
| `getBalance()` | Баланс клана |
| `getUnlockedSlots()` | Открытые слоты хранилища |
| `getHome()` | `Optional<Location>` кланового дома |

Изменение:

| Метод | Описание |
|---|---|
| `addMember(UUID)` | Добавить участника (если он уже в клане, будет ошибка) |
| `removeMember(UUID)` | Убрать участника (владельца убрать нельзя) |
| `addBalance(double)` | Пополнить баланс |
| `subtractBalance(double)` | Снять с баланса; `CompletableFuture<Boolean>`, `false` если средств не хватает |
| `incrementKills()` | +1 убийство |
| `setUnlockedSlots(int)` | Задать число открытых слотов (от 0 до размера хранилища) |
| `setHome(Location)` | Задать клановый дом |
| `disband()` | Распустить клан |

### Важно

- Все методы, которые что-то меняют, нужно вызывать **из основного потока сервера**. Из другого потока будет `IllegalStateException`. Читать можно откуда угодно, но безопаснее из основного потока.
- Методы с `CompletableFuture` завершаются после записи в базу данных. Кэш обновляется сразу.
- После `disband()` старые объекты `Clan` перестают быть актуальными: `exists()` вернёт `false`, а изменяющие методы выбросят `IllegalStateException`.
- Для кланов не открыт доступ к кланового хранилищу (сундуку).

### События

Все события вызываются в основном потоке и **не отменяются** (они срабатывают уже после изменения). Срабатывают и от команд игроков, и от вызовов API.

| Событие | Когда | Данные |
|---|---|---|
| `ClanCreateEvent` | Клан создан | `getTag()`, `getOwner()` |
| `ClanDisbandEvent` | Клан распущен | `getTag()`, `getOwner()`, `getMembers()` |
| `ClanMemberJoinEvent` | Игрок вступил в клан | `getTag()`, `getPlayer()` |
| `ClanMemberLeaveEvent` | Игрок вышел или был исключён | `getTag()`, `getPlayer()` |

Пакет событий: `com.agent1k.api.event`.

### Примеры

Награда клану игрока:

```java
GlowClansAPI api = GlowClansAPI.getInstance();

api.getClanByPlayer(player.getUniqueId()).ifPresent(clan -> {
    clan.addBalance(100);
    clan.incrementKills();
});
```

Снятие денег с клана:

```java
api.getClanByTag("MyClan").ifPresent(clan ->
    clan.subtractBalance(500).thenAccept(success -> {
        if (!success) {
            // не хватило средств
        }
    })
);
```

Создание клана:

```java
try {
    api.createClan("MyClan", player.getUniqueId())
       .thenAccept(clan -> getLogger().info("Создан клан " + clan.getTag()));
} catch (IllegalArgumentException | IllegalStateException e) {
    getLogger().warning(e.getMessage());
}
```

Слушатель событий:

```java
@EventHandler
public void onJoin(ClanMemberJoinEvent event) {
    Player player = Bukkit.getPlayer(event.getPlayer());
    if (player != null) {
        player.sendMessage("Вы вступили в клан " + event.getTag());
    }
}
```

---

## English

### Setup

Maven (via JitPack):

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.Agent1k-1</groupId>
        <artifactId>GlowClans</artifactId>
        <version>1.3</version>
        <scope>provided</scope>
    </dependency>
</dependencies>
```

Gradle:

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    compileOnly 'com.github.Agent1k-1:GlowClans:1.3'
}
```

In your plugin's `plugin.yml`:

```yaml
depend: [ GlowClans ]
# or, if the clan integration is optional:
softdepend: [ GlowClans ]
```

Version `1.3` is a git tag of the repository.

### Getting the API

```java
GlowClansAPI api = GlowClansAPI.getInstance();
```

If GlowClans is not enabled, an `IllegalStateException` is thrown.

### GlowClansAPI methods

| Method | Description |
|---|---|
| `hasClan(UUID player)` | Whether the player is in a clan |
| `clanExists(String tag)` | Whether a clan with this tag exists |
| `getClanByPlayer(UUID player)` | `Optional<Clan>` of the player's clan |
| `getClanByTag(String tag)` | `Optional<Clan>` by tag |
| `getClans()` | List of all clans |
| `createClan(String tag, UUID owner)` | Creates a clan, returns `CompletableFuture<Clan>` |

`createClan` does not charge any money and does not apply the `config.yml` restrictions (tag length, blocked names). It only checks that the tag is 1 to 64 characters without whitespace, that no clan with this tag exists, and that the owner is not already in a clan. Otherwise an `IllegalArgumentException` or `IllegalStateException` is thrown.

### Clan methods

`Clan` is a live handle: data is read from the plugin cache on every call.

Reading:

| Method | Description |
|---|---|
| `getTag()` | Clan tag |
| `exists()` | Whether the clan still exists |
| `getOwner()` | Owner UUID |
| `getMembers()` | UUIDs of all members (including the owner) |
| `getMemberCount()` | Number of members |
| `isMember(UUID)` / `isOwner(UUID)` | Member / owner check |
| `getKills()` | Clan kills |
| `getBalance()` | Clan balance |
| `getUnlockedSlots()` | Unlocked storage slots |
| `getHome()` | `Optional<Location>` of the clan home |

Modifying:

| Method | Description |
|---|---|
| `addMember(UUID)` | Add a member (fails if they are already in a clan) |
| `removeMember(UUID)` | Remove a member (the owner cannot be removed) |
| `addBalance(double)` | Add to the balance |
| `subtractBalance(double)` | Take from the balance; `CompletableFuture<Boolean>`, `false` if funds are insufficient |
| `incrementKills()` | +1 kill |
| `setUnlockedSlots(int)` | Set the number of unlocked slots (0 up to the storage size) |
| `setHome(Location)` | Set the clan home |
| `disband()` | Disband the clan |

### Notes

- Every method that changes something must be called **from the main server thread**, otherwise an `IllegalStateException` is thrown. Reading works from any thread, but the main thread is safer.
- Methods returning `CompletableFuture` complete after the database write. The cache is updated immediately.
- After `disband()`, old `Clan` objects are stale: `exists()` returns `false` and modifying methods throw `IllegalStateException`.
- The clan storage (chest) is not exposed through the API.

### Events

All events are fired on the main thread and are **not cancellable** (they fire after the change has been made). They fire for both player commands and API calls.

| Event | When | Data |
|---|---|---|
| `ClanCreateEvent` | A clan was created | `getTag()`, `getOwner()` |
| `ClanDisbandEvent` | A clan was disbanded | `getTag()`, `getOwner()`, `getMembers()` |
| `ClanMemberJoinEvent` | A player joined a clan | `getTag()`, `getPlayer()` |
| `ClanMemberLeaveEvent` | A player left or was kicked | `getTag()`, `getPlayer()` |

Events package: `com.agent1k.api.event`.

### Examples

Reward a player's clan:

```java
GlowClansAPI api = GlowClansAPI.getInstance();

api.getClanByPlayer(player.getUniqueId()).ifPresent(clan -> {
    clan.addBalance(100);
    clan.incrementKills();
});
```

Take money from a clan:

```java
api.getClanByTag("MyClan").ifPresent(clan ->
    clan.subtractBalance(500).thenAccept(success -> {
        if (!success) {
            // not enough funds
        }
    })
);
```

Create a clan:

```java
try {
    api.createClan("MyClan", player.getUniqueId())
       .thenAccept(clan -> getLogger().info("Created clan " + clan.getTag()));
} catch (IllegalArgumentException | IllegalStateException e) {
    getLogger().warning(e.getMessage());
}
```

Listen to events:

```java
@EventHandler
public void onJoin(ClanMemberJoinEvent event) {
    Player player = Bukkit.getPlayer(event.getPlayer());
    if (player != null) {
        player.sendMessage("You joined clan " + event.getTag());
    }
}
```
