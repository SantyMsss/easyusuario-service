package co.edu.uceva.celularservice.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que almacena los encodings faciales de los usuarios
 * para autenticación mediante reconocimiento facial con DeepFace
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "face_encodings")
public class FaceEncoding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    @JsonIgnoreProperties({"ingresos", "gastos", "metasAhorro", "hibernateLazyInitializer", "handler"})
    private Usuario usuario;

    /**
     * Embedding facial generado por DeepFace (vector de 128-4096 dimensiones)
     * Almacenado como JSON array
     */
    @Column(name = "face_embedding", columnDefinition = "TEXT", nullable = false)
    private String faceEmbedding;

    /**
     * Ruta donde se almacena la imagen facial de referencia
     */
    @Column(name = "face_image_path")
    private String faceImagePath;

    /**
     * Modelo de DeepFace utilizado (VGG-Face, Facenet, OpenFace, DeepFace, DeepID, ArcFace, Dlib, SFace)
     */
    @Column(name = "model_name")
    private String modelName = "Facenet512"; // Modelo por defecto (más preciso)

    /**
     * Métrica de distancia utilizada (cosine, euclidean, euclidean_l2)
     */
    @Column(name = "distance_metric")
    private String distanceMetric = "cosine";

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Indica si este encoding está activo (permite deshabilitar sin borrar)
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        isActive = true;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
