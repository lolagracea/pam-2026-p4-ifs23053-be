CREATE TABLE IF NOT EXISTS plants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nama VARCHAR(100) NOT NULL,
    path_gambar VARCHAR(255) NOT NULL,
    deskripsi TEXT NOT NULL,
    manfaat TEXT NOT NULL,
    efek_samping TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);


CREATE TABLE destinations (
                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),   -- sesuai UUID, bisa auto-generate

                              name VARCHAR(100) NOT NULL,
                              country VARCHAR(100) NOT NULL,
                              city VARCHAR(100),                               -- nullable
                              description TEXT,                                -- nullable
                              path_gambar VARCHAR(255),                        -- nullable (pathGambar di Kotlin)

                              created_at TIMESTAMPTZ NOT NULL DEFAULT now(),   -- Instant -> timestamp with time zone
                              updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);