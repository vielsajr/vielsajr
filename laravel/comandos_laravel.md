## Checklist

1. Preparar proyecto
   - Crear el proyecto y entrar en la carpeta
   - Dar permisos a storage y bootstrap/cache
   - Crear carpeta para archivos públicos y dar permisos
   - Configurar .env (base de datos, locales, timezone)
   - Generar clave de aplicación si no existe
   - Limpiar cache de configuración
     </br>
2. Configurar sistema de archivos
   </br>
3. Instalar paquetes necesarios
   - Traducciones
   - Autenticación con Breeze
   - Roles y permisos (Spatie)
     </br>
4. Migraciones y relaciones
   - Crear migraciones de tablas
   - Definir relaciones en los modelos
     </br>
5. Seeders y Factories
   </br>
6. Rutas y controladores
   </br>

   Si al mostrar vistas da error de permisos:

   ```bash
   sudo chown -R www-data:www-data storage bootstrap/cache
   sudo chmod -R 775 storage bootstrap/cache
   ```

   ```bash
   php artisan config:clear
   php artisan cache:clear
   php artisan route:clear

   ```

## Crear proyecto

```bash
composer create-project laravel/laravel nombre_proyecto
cd nombre_proyecto
sudo chown -R www-data:www-data storage bootstrap/cache
sudo chmod -R 775 storage bootstrap/cache
```

---

## Configurar `config/filesystems.php`

```php
'nombre_almacenamiento' => [
            'driver' => 'local',
            'root' => public_path('assets/imagenes'),
            'visibility' => 'public'
        ],
```

Damos permisos de lectura y escritura a todos los usuarios (777) para la carpeta y su contenido

```bash
mkdir -p public/assets/imagenes
chmod -R 777 public/assets/imagenes
```

---

## Configurar `.env`

```
APP_LOCALE=es
APP_FALLBACK_LOCALE=es
APP_FAKER_LOCALE=es_ES
APP_TIMEZONE=Europe/Madrid

DB_CONNECTION=mysql
DB_HOST=127.0.0.1
DB_PORT=3306
DB_DATABASE=nombre_bbdd
DB_USERNAME=root
DB_PASSWORD=root
```

Si `APP_KEY` no tiene valor:

```bash
php artisan key:generate
```

Siempre que modifiquemos el `.env`, es recomendable hacer:

```bash
php artisan config:clear
```

---

## Instalar traducciones

```bash
composer require laraveles/spanish
php artisan laraveles:install-lang
```

---

## Instalar Laravel/Breeze

**IMPORTANTE:** Si no existe, crear el fichero `resources/views/welcome.blade.php`

```bash
composer require laravel/breeze --dev
php artisan breeze:install blade
php artisan migrate
npm install
npm run dev
```

Si accedemos a la ruta login, introducimos unos datos y éstos son correctos, se creará la sesión del usuario y se le redirigirá a la ruta "/dashboard".

Si queremos cambiar esta ruta Modificar el método store del controlador Auth/AuthenticatedSessionController:

```php
public function store(LoginRequest $request): RedirectResponse
{
  $request->authenticate();
  $request->session()->regenerate();
  return redirect()->intended('/');
}
```

---

## Instalar laravel-permission

```bash
composer require spatie/laravel-permission
php artisan vendor:publish --provider="Spatie\Permission\PermissionServiceProvider"
php artisan config:clear
php artisan migrate
```

Para poder usar el middleware, hay que definirlo en `bootstrap/app.php`:

```php
->withMiddleware(function (Middleware $middleware) {
         $middleware->alias([
          'role' => \Spatie\Permission\Middleware\RoleMiddleware::class
     ]);
})
```

Es importante modificar el modelo `User` para incluir los roles

```php
use Spatie\Permission\Traits\HasRoles;

class User extends Authenticatable
{
    use HasRoles;
}
```

Crear los roles en el seeder y asignarlos a un usuario:

```php
use Spatie\Permission\Models\Role;

Role::create(['name' => 'gestor']);
Role::create(['name' => 'admin']);
// ---------
$user->assignRole('admin');
```

Para proteger una ruta en `routes/web.php`:

```php
Route::post('posts/crear', [PostController::class, 'store'])->name('posts.store')->middleware('auth');
Route::get('posts/{post}/editar', [PostController::class, 'edit'])->name('posts.edit')->middleware(['auth', 'role:admin|gestor']);
Route::post('tags/{tag}', [TagController::class, 'store'])->name('tags.store')->middleware(['auth', 'role:administrador']);
```

Si da algún error en la creación de los roles podemos intentar limpiar la caché:

```bash
php artisan cache:forget spatie.permission.cache
```

---

## Paginación

Copiando el siguiente código a una plantilla blade, añadimos un menú de paginación

```php
{{$colleccion_a_paginar->links('pagination::bootstrap-5')}}
```

Para modificar las vistas de paginación:

```bash
php artisan vendor:publish --tag=laravel-pagination
```

En `bootstrap-5.blade.php` podemos cambiar el texto de paginación

---

## Migraciones y relaciones

**DOCUMENTACION**: Available Column Types

### Uno a uno

```php
// User (Clase "principal")
public function profile()
{
    return $this->hasOne(Profile::class);
}
```

```php
// Profile
public function user()
{
    return $this->belongsTo(User::class);
}
```

---

### Uno a muchos

```php
// User
public function posts()
{
    return $this->hasMany(Post::class);
}
```

```php
// Post
public function user()
{
    return $this->belongsTo(User::class);
}
```

---

### Muchos a muchos

Es necesario crear una migración para la tabla intermedia

```bash
php artisan make:migration create_post_tag_table
```

```php
public function up(): void
    {
        Schema::create('post_tag', function (Blueprint $table) {
            $table->foreignId('post_id');
            $table->foreign('post_id')->references('id')->on('posts')->onDelete('cascade');
            $table->foreignId('tag_id');
            $table->foreign('tag_id')->references('id')->on('tags')->onDelete('cascade');
            $table->primary(['post_id', 'tag_id']);
        });
    }
```

```php
// Tag
public function posts()
{
    return $this->belongsToMany(Post::class);
}
```

```php
// Post
public function tags()
{
    return $this->belongsToMany(Tag::class);
}
```

---

## Factories y Seeders

```bash
php artisan make:factory NombreFactory
php artisan make:seeder NombreSeeder
```

---

### DatabaseSeeder

Siempre se borran primero las tablas que dependen de otras.

1. Tablas pivot (muchos a muchos)

2. Tablas hijas

3. Tablas principales

4. Tablas de configuración (roles, permissions)

Para crear, es justo al revés: primero lo que otros necesitan.

```php
<?php
use Illuminate\Support\Facades\DB;

class DatabaseSeeder extends Seeder
{
    public function run(): void
    {
        DB::table('users')->delete();
        DB::table('post_tag')->delete();
        DB::table('tags')->delete();
        DB::table('posts')->delete();

        $this->call(Tags::class);
        Post::factory(20)->create();

        $this->call(UserSeeder::class);
        User::factory(5)->create();
        User::factory()->create([
            'name' => 'ejemplo',
            'email' => 'ejemplo@ejemplo.com',
            'password' => bcrypt('ejemplo1234'),
        ]);
    }
}
```

---

### Factory

```php
use Illuminate\Support\Str;

class PostFactory extends Factory
{
    public function definition(): array
    {
        $nombre = $this->faker->name;

        return [
            'nombre' => $nombre,
            'slug' => Str::slug($nombre)
        ];
    }
}
```

```php
use Illuminate\Database\Eloquent\Factories\HasFactory;
class Post extends Model
{
    use HasFactory;
    // Code
}
```

---

### Seeder

```php
use Spatie\Permission\Models\Role;

class UserSeeder extends Seeder
{
    private $usuarios = [
        [
            'name' => 'gestor',
            'email' => 'gestor@educastur.es',
            'password' => 'gestor1234',
            'role' => 'gestor'
        ],
        [
            'name' => 'admin',
            'email' => 'admin@educastur.es',
            'password' => 'admin1234',
            'role' => 'administrador'
        ]
    ];

    public function run(): void
    {
        Role::create(['name' => 'gestor']);
        Role::create(['name' => 'administrador']);
        foreach ($this->usuarios as $usuario) {
            $u = new User();
            $u->name = $usuario['name'];
            $u->email = $usuario['email'];
            $u->password = bcrypt($usuario['password']);
            $u->save();
            $u->assignRole($usuario['role']);
        }
        $this->command->info('Tabla usuarios inicializada con datos');
    }
}
```

---

## Métodos y helpers útiles

Sustituye el parametro de busqueda por el especificado (slug, por ejemplo)

```php
public function getRouteKeyName()
{
  return 'slug';
}
```

---

```php
use Carbon\Carbon;

public function getEdad()
{
 $fechaFormateada = Carbon::parse($this->fechaNacimiento);
  // return $fechaFormateada->diffInYears(Carbon::now());
  return $fechaFormateada->age;
}
```

---

### Consultar datos

**DOCUMENTACION**: Database: Query Builder

#### Búsquedas frecuentes

```php
User::all(); // Devuelve todos los usuarios
User::paginate(5); // Devuelve una coleccion paginada de 5 usuarios por página
User::find(1); // Busca por Id (PK)
User::findOrFail(1); // Busca por Id o lanza 404 si falla
User::first(); // Devuelve el primer usuario
User::firstOrFail(); // Devuelve el primer usuario o lanza 404 si falla

User::where('email', $email)->first();
User::where('votes', '>', 100)->firstOrFail();
User::where('votes', '>', 100)->take(10)->get(); // Obtener 10 usuarios con más de 100 votos

User::where('votes', '>', 100)->count();
Orders::max('price');
Orders::min('price');
Orders::avg('price');
User::sum('votes');
```

#### Condicionales

```php
User::where('activo', true)->exists();
User::where('email', $email)->doesntExist();
```

#### Orden y límites

```php
Post::orderBy('created_at', 'desc')->get();
Post::latest()->take(5)->get();
Post::oldest()->first();
```

---

### Insertar datos

```php
$user = new User();
$user->name = "Juan";
$user->save();
```

---

### Actualizar datos

```php
$user = User::find(1);
$user->email = "juan@gmail.com";
$user->save();
```

Podemos usar update() pasando un array con los datos a actualizar (más limpio si son muchos)

```php
$user = User::find(1);
$user->update([
    'email' => 'juan@gmail.com'
]);
```

---

### Borrar datos

```php
User::find(1)->delete();
```

```php
User::where('votes', '>', 100)->delete();
```

---
