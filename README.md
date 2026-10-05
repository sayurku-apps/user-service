# user-service

Bagian dari [SAYURKU](https://github.com/sayurku-apps), e-commerce sayuran segar berarsitektur microservices (Spring Boot 4, Java 26, PostgreSQL).

Service ini mengurus **akun**: register, login (membuat JWT), profil, alamat kirim, dan pengaturan role oleh ADMIN.

- Port: `8081`
- Database: `sayurku_user_db`
- Dipanggil dari luar lewat [api-gateway](https://github.com/sayurku-apps/api-gateaway) (`:8080`)

## Cara kerja login

1. `POST /api/auth/login` mengembalikan JWT berisi `sub` = email, `uid` = id user, `role`, dan `branchId` (khusus STAFF).
2. Request berikutnya membawa `Authorization: Bearer <token>`. **api-gateway** yang memeriksa token, lalu meneruskan identitasnya lewat header `X-User-Id`, `X-User-Email`, `X-User-Role`, `X-User-Branch-Id`.
3. Service ini cukup membaca header tersebut (`GatewayAuthFilter`). Gateway selalu membuang header yang dikirim client, jadi header ini tidak bisa dipalsukan dari luar.

## Endpoint

| Method | Path | Akses | Keterangan |
|---|---|---|---|
| POST | `/api/auth/register` | publik | `{name, email, password, phone}`. Role awal CUSTOMER |
| POST | `/api/auth/login` | publik | `{email, password}` → `{token, name, email, role, branchId}` |
| GET | `/api/auth/me` | login | Profil sendiri |
| GET | `/api/users/me/addresses` | login | Daftar alamat (alamat utama di atas) |
| POST | `/api/users/me/addresses` | login | `{label, recipientName, phone, street, city, province, postalCode, isDefault}` |
| PUT | `/api/users/me/addresses/{id}` | login | Ubah alamat |
| PATCH | `/api/users/me/addresses/{id}/default` | login | Jadikan alamat utama |
| DELETE | `/api/users/me/addresses/{id}` | login | Hapus alamat |
| GET | `/api/users?role=STAFF&page=&size=` | ADMIN | Daftar user |
| PATCH | `/api/users/{id}/role` | ADMIN | `{role, branchId}`. STAFF wajib punya cabang (dicek ke product-service) |
| GET | `/internal/users/{uid}/addresses/{id}` | antar-service | Dipakai order-service saat checkout |

Perubahan role baru berlaku setelah user tersebut **login ulang**, karena role tersimpan di dalam token.

ADMIN pertama dibuat lewat SQL, karena belum ada ADMIN yang bisa mengangkatnya:

```sql
UPDATE users SET role = 'ADMIN' WHERE email = 'admin@contoh.com';
```

## Menjalankan

Butuh Java 26 dan PostgreSQL dengan database `sayurku_user_db`.

```bash
cp src/main/resources/application-local.properties.example src/main/resources/application-local.properties
# isi password database, jwt.secret (sama dengan api-gateway), internal.token (sama di semua service)
./mvnw spring-boot:run        # Windows PowerShell: .\mvnw.cmd spring-boot:run
```

Cara paling mudah menjalankan semua service sekaligus: Docker Compose di repo [infra](https://github.com/sayurku-apps/infra).

## Konfigurasi

| Properti | Env var | Keterangan |
|---|---|---|
| `spring.datasource.password` | `SPRING_DATASOURCE_PASSWORD` | Password Postgres |
| `jwt.secret` | `JWT_SECRET` | Minimal 32 karakter, harus sama dengan api-gateway |
| `internal.token` | `INTERNAL_TOKEN` | Rahasia bersama untuk `/internal/**` (header `X-Internal-Token`) |
| `services.product.url` | `PRODUCT_SERVICE_URL` | Default `http://localhost:8082` |

## Database

Skema dikelola **Flyway** (`src/main/resources/db/migration`). Perubahan skema = file baru `V3__...sql`, jangan mengubah file lama. Hibernate hanya memvalidasi (`ddl-auto=validate`).

## Tes

```bash
./mvnw test
```

Tes end-to-end lintas service ada di repo infra (`tests/e2e_*.py`).
