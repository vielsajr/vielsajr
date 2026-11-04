Si da el error:

> The stream or file "/var/www/html/storage/logs/laravel.log" could not be opened:
> failed to open stream: Permission denied

```bash
sudo chown -R www-data:www-data storage bootstrap/cache
sudo chmod -R 775 storage bootstrap/cache
```

```bash
php artisan config:clear
php artisan cache:clear
php artisan route:clear
```

---

## Crear proyecto para API

```bash
composer create-project laravel/laravel laravel_drones
cd laravel_drones
php artisan install:api
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

---

## Instalar laravel-permission

```bash
composer require spatie/laravel-permission
php artisan vendor:publish --provider="Spatie\Permission\PermissionServiceProvider"
php artisan config:clear
php artisan migrate
```

---

## Configurar proyecto

### Configurar `.env`

```
APP_LOCALE=es
APP_FALLBACK_LOCALE=es
APP_FAKER_LOCALE=es_ES

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

### Ver rutas API

```bash
php artisan route:list --path=api
```

Si no aparecen las rutas de la api, comprobar que en `bootstrap/app.php` exista lo siguiente:

```php
->withRouting(
        web: __DIR__ . '/../routes/web.php',
        api: __DIR__ . '/../routes/api.php', // <- IMPORTANTE
        commands: __DIR__ . '/../routes/console.php',
        health: '/up',
    )
```

### Ver excepciones en formato JSON

En `bootstrap/app.php` añadimos:

```php
use Illuminate\Http\Request;

->withExceptions(function (Exceptions $exceptions) {
    $exceptions->shouldRenderJsonWhen(function (Request $request, Throwable $e) {
        if ($request->is('api/*')) {
            return true;
        }
        return $request->expectsJson();
    });
```

## API

### Rutas `routes/api.php`

Un enfoque común es el uso del método Route::apiResource() para generar automáticamente rutas RESTful.
El nombre del parametro se simplifica al singular como lo haria en inglés. Se puede modificar con el metodo `parameters()`.
Para proteger una ruta con token, añadimos el `middleware` de _Sanctum_.

```php
Route::apiResource('animales', AnimalApiController::class)
->parameters(['animales' => 'animal'])
->middleware('auth:sanctum');
```

O se pueden gestionar de manera individual:

```php
Route::get('animales', [AnimalApiController::class, 'index']);
Route::get('animales/{animal}', [AnimalApiController::class, 'show']);
Route::post('animales', [AnimalApiController::class, 'store'])->middleware('auth:sanctum');
Route::put('animales/{animal}', [AnimalApiController::class, 'update'])->middleware('auth:sanctum');
Route::delete('animales/{animal}', [AnimalApiController::class, 'destroy'])->middleware('auth:sanctum');
```

Otra opción es agrupar por middleware:

```php
Route::get('animales', [AnimalApiController::class, 'index']);
Route::get('animales/{animal}', [AnimalApiController::class, 'show']);

Route::middleware('auth:sanctum')->group(function () {
    Route::apiResource('animales', AnimalApiController::class)
    ->parameters(['animales' => 'animal'])
    ->only(['store', 'update', 'destroy']);
});

```

---

### Controlador

Podemos crear controladores específicos para nuestra API directamente con los métodos index, store, show, update y destroy:

```bash
php artisan make:controller AnimalApiController --api
```

Estructura mínima:

```php
class AnimalApiController extends Controller
{
    public function index()
    {
        $animales = Animal::with(['cuidadores', 'revisiones']);
        return AnimalResource::collection(
            $animales->paginate(5)
        );
    }

    public function store(StoreAnimalRequest $request)
    {
        $datos = $request->validated();

        $datos['slug'] = Str::slug($datos['especie']);

        $animal = Animal::create($datos);

        $cuidadoresIds = Cuidador::inRandomOrder()->limit(2)->pluck('id');

        $animal->cuidadores()->attach($cuidadoresIds);

        return new AnimalResource($animal->load(['cuidadores', 'revisiones']));
    }

    public function show(Animal $animal)
    {
        return new AnimalResource($animal->load(['cuidadores', 'revisiones']));
    }

    public function update(UpdateAnimalRequest $request, Animal $animal)
    {
        $datos = $request->validated();
        if (isset($datos['especie']))
            $datos['slug'] = Str::slug($datos['especie']);

        $animal->update($datos);

        // Opcional: Si quieres cambiar los cuidadores al actualizar
        // $cuidadoresIds = Cuidador::inRandomOrder()->limit(2)->pluck('id');
        // $animal->cuidadores()->sync($cuidadoresIds);

        return new AnimalResource($animal->load(['cuidadores', 'revisiones']));
    }

    // Para lanzar un mensaje personalizado si no existe:
    // public function destroy(Animal $animal)
    // {
    //     if (!$animal) return response()->json(['mensaje' => 'Error']);

    //     $animal->cuidadores()->detach();
    //     $animal->delete();
    //     return response()->json(['mensaje' => 'Animal eliminado']);
    // }

    // Método destroy simplificado:
    public function destroy(Animal $animal)
    {
    $animal->cuidadores()->detach(); // Si no se establece condicion onDelete() en la tabla pivot
    $animal->delete();

    return response()->json(null, 204);
    }
}
```

#### Respuestas JSON manuales

Cuando no necesitamos devolver un modelo (API Resource), podemos usar el modelo de respuesta manual `response()->json()`:

```php
// ERROR
return response()->json(['error' => 'No encontrado'], 404);

// ELIMINAR
return response()->json(null, 204);

```

A continuación, se muestran algunos códigos útiles

| Código | Significado      |
| ------ | ---------------- |
| 200    | OK               |
| 201    | Creado           |
| 204    | Sin contenido    |
| 401    | No autenticado   |
| 403    | No autorizado    |
| 404    | No encontrado    |
| 422    | Error validación |

---

### Model

Hay que definir los atributos del modelo en un fillable para evitar la _`MassAssignmentException`_

```php
protected $fillable = ['especie', 'peso', 'descripcion'];

// Los campos definidos en $hidden no se muestran en la respuesta GET (si no usa Resource)
protected $hidden = ['created_at', 'updated_at'];
```

---

### Resources

Permite devolver un formato JSON más limpio

```bash
php artisan make:resource AnimalResource
```

En `app/Http/Resources/AnimalResource.php`:

```php
public function toArray($request)
{
    return [
        'id' => $this->id,
        'especie' => $this->especie,
        'peso' => $this->peso,
        'fechaNacimiento' => $this->fechaNacimiento,
    ];
}
```

Si devolvemos un recurso de un elemento concreto, se mapean los atributos del elemento con los del recurso
y sólo se muestran los definidos en ambos.

```php
return new AnimalResource($animal);
```

El método estatico `collection()` aplica la lógica del Resource a cada uno de los elementos de la colección.

```php
return AnimalResource::collection(Animal::paginate(4));
```

Si queremos añadir metadatos que afecten a toda la colección y no a cada elemento individual, habrá que crear una clase dedicada:

```bash
php artisan make:resource AnimalCollection
```

---

### Validación con FormRequest

```bash
php artisan make:request StoreAnimalRequest
php artisan make:request UpdateAnimalRequest
```

#### Store

```php
class StoreArticuloRequest extends FormRequest
{
    public function authorize()
    {
        return true; // IMPORTANTE PONERLO A TRUE
    }

    public function rules()
    {
        return [
            'especie' => 'required|unique:animales',
            'peso' => 'required|numeric|min:0',
            'altura' => 'required|numeric|min:0',
            'fechaNacimiento' => 'required|date|before_or_equal:now',
        ];
    }
}
```

#### Update (PUT vs PATCH)

```php
class UpdateArticuloRequest extends FormRequest
{
    public function authorize()
    {
        return true;
    }

    public function rules()
    {
        $animalID = $this->route('animal')->id
        if ($this->method() == 'PUT') {
            return [
                'especie' => [
                    'required',
                    'min:3',
                    Rule::unique('animales', 'especie')->ignore($animalID),
                ],
                'peso' => 'required|numeric|min:0',
                'altura' => 'required|numeric|min:0',
                'fechaNacimiento' => 'required|date|before_or_equal:now',
            ];
        }

        return [
                'especie' => [
                    'sometimes',
                    'min:3',
                    Rule::unique('animales', 'especie')->ignore($animalID),
                ],
                'peso' => 'sometimes|numeric|min:0',
                'altura' => 'sometimes|numeric|min:0',
                'fechaNacimiento' => 'sometimes|date|before_or_equal:now',
            ];
    }
}

```

---

## Generación de tokens y habilidades (abilities)

En ` routes/web.php` crearemos una ruta get para devolver los tokens creados

```php
Route::get('/profile/crearToken', [ProfileController::class, 'crearToken'])
 ->name('profile.crearToken')->middleware('auth');
```

En `ProfileController` definiremos el metodo especificado en la ruta (_crearToken_):

```php
public function crearToken(Request $request)
{
    $request->user()->tokens()->delete(); // Borra los tokens asignados previamente al usuario actual

    $tokenSimple = $request->user()->createToken('token-acceso');

    $tokenInsertar = $request->user()->createToken('token-insertar', ['insertar-animales']);
    $tokenBorrar = $request->user()->createToken('token-borrar', ['borrar-animales']);
    return [
            'token-acceso' => $tokenSimple->plainTextToken,
            'token-insertar' => $tokenInsertar->plainTextToken,
            'token-borrar' => $tokenBorrar->plainTextToken
        ];
 }
```

### Crear tokens según el rol:

```php
public function crearToken(Request $request)
{
    $user = $request->user();

    // Elimina tokens anteriores
    $user->tokens()->delete();

    $tokens = [];

    if ($user->hasRole('admin')) {

        $adminToken = $user->createToken(
            'token-admin',
            ['admin-vuelos', 'gestionar-usuarios', 'gestionar-circuitos']
        );

        $tokens['token-admin'] = $adminToken->plainTextToken;
    }

    if ($user->hasRole('user')) {

        $userToken = $user->createToken(
            'token-user',
            ['ver-vuelos']
        );

        $tokens['token-user'] = $userToken->plainTextToken;
    }

    // Token básico común (opcional)
    $basicToken = $user->createToken('token-acceso');

    $tokens['token-acceso'] = $basicToken->plainTextToken;

    return response()->json($tokens);
}
```


Podemos comprobar si un usuario tiene tokens con una habilidad concreta:

```php
$user->tokenCan('crear-articulo');
```

Podemos especificar en un middleware si está protegido por token con `auth:sanctum` y si tiene al menos
una o todas las habilidades especificadas:

**`abilities`**: El token debe tener todas las abilities listadas para tener acceso.

```php
->middleware(['auth:sanctum', 'abilities:insertar-animales,borrar-animales']);
```

**`ability`**: El token debe tener al menos una de las abilities listadas para tener acceso.

```php
->middleware(['auth:sanctum', 'ability:insertar-animales,borrar-animales']);
```

En `bootstrap/app.php`:

```php
use Laravel\Sanctum\Http\Middleware\CheckAbilities;
use Laravel\Sanctum\Http\Middleware\CheckForAnyAbility;

->withMiddleware(function (Middleware $middleware) {
    $middleware->alias([
        'role' => \Spatie\Permission\Middleware\RoleMiddleware::class,
        'abilities' => CheckAbilities::class,
        'ability' => CheckForAnyAbility::class,
    ]);
})
```

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
            $table->foreignId('post_id')->constrained('posts')->onDelete('cascade'); // Si el singular varía, añadirlo como parametro en constrained()
            $table->foreignId('tag_id')->constrained('tags')->onDelete('cascade');
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

### Ajuste del Modelo `User`

Es importante modificar el modelo `User` para incluir los roles y los tokens

```php
use Spatie\Permission\Traits\HasRoles;

class User extends Authenticatable
{
    use HasRoles;
    use HasApiTokens;
}
```

Crear los roles en el seeder y asignarlos a un usuario:

```php
use Spatie\Permission\Models\Role;

Role::create(['name' => 'gestor']);
Role::create(['name' => 'admin']);
```

```php
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

php artisan make:model Participante -ms
php artisan make:model Tanda -ms
php artisan make:model Penalizacion -ms
php artisan make:model Resultado -ms
php artisan make:migration create_penalizacion_resultado_table
