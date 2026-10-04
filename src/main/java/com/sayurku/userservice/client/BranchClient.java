package com.sayurku.userservice.client;

import com.sayurku.userservice.exception.UpstreamException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.UUID;

// Cabang ada di product-service. Dipakai untuk memastikan cabang STAFF benar-benar ada,
// supaya tidak ada pegawai yang ditempatkan di cabang salah ketik.
@Component
public class BranchClient {

    private final RestClient http;

    public BranchClient(@Value("${services.product.url}") String baseUrl) {
        this.http = RestClient.builder().baseUrl(baseUrl).build();
    }

    /** true kalau cabang ada dan aktif (GET /api/branches/{id} publik, dipanggil langsung tanpa gateway) */
    public boolean exists(UUID branchId) {
        try {
            http.get().uri("/api/branches/{id}", branchId).retrieve().toBodilessEntity();
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            return false;
        } catch (RestClientResponseException e) {
            throw new UpstreamException(HttpStatus.BAD_GATEWAY, "product-service membalas error " + e.getStatusCode().value());
        } catch (ResourceAccessException e) {
            throw new UpstreamException(HttpStatus.SERVICE_UNAVAILABLE, "product-service tidak bisa dihubungi");
        }
    }
}
