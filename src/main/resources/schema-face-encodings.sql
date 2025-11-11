-- Script SQL para la tabla face_encodings
-- Esta tabla almacena los encodings faciales de los usuarios para autenticación KYC

CREATE TABLE IF NOT EXISTS face_encodings (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE,
    face_embedding TEXT NOT NULL,
    face_image_path VARCHAR(500),
    model_name VARCHAR(50) DEFAULT 'Facenet512',
    distance_metric VARCHAR(20) DEFAULT 'cosine',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    
    CONSTRAINT fk_face_encoding_usuario 
        FOREIGN KEY (usuario_id) 
        REFERENCES usuarios(id) 
        ON DELETE CASCADE
);

-- Índices para optimizar consultas
CREATE INDEX idx_face_encoding_usuario_id ON face_encodings(usuario_id);
CREATE INDEX idx_face_encoding_active ON face_encodings(is_active);
CREATE INDEX idx_face_encoding_usuario_active ON face_encodings(usuario_id, is_active);

-- Comentarios para documentación
COMMENT ON TABLE face_encodings IS 'Almacena los embeddings faciales para autenticación biométrica';
COMMENT ON COLUMN face_encodings.face_embedding IS 'Vector de características faciales en formato JSON array';
COMMENT ON COLUMN face_encodings.model_name IS 'Modelo DeepFace utilizado (Facenet512, VGG-Face, ArcFace, etc.)';
COMMENT ON COLUMN face_encodings.distance_metric IS 'Métrica para comparación (cosine, euclidean, euclidean_l2)';
COMMENT ON COLUMN face_encodings.is_active IS 'Indica si este encoding está activo para autenticación';
