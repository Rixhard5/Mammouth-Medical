# Mammouth Medical Online Pharmacy

### This is the development platform for an online pharmacy application, mainly developed for android devices.

## Apps used for dev:

1. [**Android Studio**](https://developer.android.com/studio/releases) | versions used:
    - *Iguana-2023.2.1*
    - *Jellyfish-2023.3.1*

3. [**Google Firebase**](https://firebase.google.com) | for storing data

4. [**Github**](https://github.com/Rixhard5/Mammouth-Medical) | version control

5. **Medical item references from:** [Chemistdirect](https://www.chemistdirect.co.uk)


## Program Explanation - Functions

> ### HU
Ez az Android-os program egy Gyógyszertár alkalmazást valósít meg, ahol különféle egészségügyi termékeket lehet vásárolni, különböző kategóriákban.

Van lehetőség *regisztrációra* és *bejelentkezésre* -> felhasználókezelés (akár Google fiókkal is).
Ezeket az adatokat lehet *frissíteni* is (kivéve email címet, azonosítás érdekében), illetve lehet *törölni* is a fiókot.

#### Adatmodellek:
  - Login-hoz tartozó modellek
  - CartItem: egy kosárhoz adott termék leírását szolgáló modell
  - Item: egy termék leírását szolgáló modell
  - User: egy felhasználót leíró modell
<br>

#### Használt layout/eszköz típusok:
  - ConstraintLayout
  - FrameLayout
  - Toolbar
  - RecyclerView
  - ScrollView
  - RelativeLayout
  - LinearLayout
  - Menu
<br>

#### Animációk:
  - Login fragment: beúszás alulról/kiuszás lefelé
  - ShopList Itemek: oldalról beúszás
  - Cart Itemek: oldalról beúszás
<br>

#### Lifecycle Hook-ok:
  - LoginFragment `onPause()`: beállítja a *SharedPreferences* értékét
  - LoginFragment `onDestroyView()`: leállítja a fregmens működését
<br>

#### Permission erőforrás - Értesítés:
  - `INTERNET`
  - `POST_NOTIFICATIONS`: sikeres regisztráció esetén értesítést kapunk
<br>

#### CRUD Műveletek:
  - `Create`: felhasználó létrehozás, egy termék kosárba rakása
  - `Read`: felhasználó adatainak lekérése, termékek lekérése, kosár elemek lekérése
  - `Update`: felhasználó adatainak frissítése, kosárból egy elem törlése frissíti az adott termék darabszámát
  - `Delete`: felhasználó törlése, termék törlése a kosárból
<br>

#### Complex Firestore lekérdezések:
  - `Szűrés`: `whereEqualTo` van használva olyan helyeken, ahol feltétel szerinti szűrés van alkalmazva
  - `Rendezés`: név szerinti rendezés kategória szűrés esetén
  - `Limitálás`: kategória szűrés esetén `5`-re van limitálva az eredmény
___

> ### EN
This Android program implements a Pharmacy application, where several medical product can be bought in different categories.

There is a possibility to *register* or *login* to the app (even with Google acc.).
These user datas can also be *updated* (except the email address) and they can also be *deleted*.

#### Data models:
  - Login model
  - CartItem: describes a cart item
  - Item: describes a medical product
  - User: desrcibes a user
<br>

#### Used layouts/tools:
  - ConstraintLayout
  - FrameLayout
  - Toolbar
  - RecyclerView
  - ScrollView
  - RelativeLayout
  - LinearLayout
  - Menu
<br>

#### Animations:
  - Login fragment: float from/to bottom
  - ShopList Items: float from side
  - Cart Items: float from side
<br>

#### Lifecycle Hooks:
  - LoginFragment `onPause()`: sets *SharedPreferences*
  - LoginFragment `onDestroyView()`: destroys the fragment
<br>

#### Permission - Notification:
  - `INTERNET`
  - `POST_NOTIFICATIONS`: we get a notification if the registration was successful
<br>

#### CRUD Operations:
  - `Create`: user creation, putting an item to the cart
  - `Read`: reading user data, reading products, reading cart items
  - `Update`: updating user data, removing an item from the basket updates the database
  - `Delete`: deleting user, removing item from cart
<br>

#### Complex Firestore queries:
  - `Filter`: `whereEqualTo` is usually used in the code where category filtering is present
  - `OrderBy`: ordering by name when filtering by category
  - `Limit`: limiting the result by `5` when filtering by category
<br>

___
*--EOF--*
