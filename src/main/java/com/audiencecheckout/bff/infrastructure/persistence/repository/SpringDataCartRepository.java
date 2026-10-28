package com.audiencecheckout.bff.infrastructure.persistence.repository;

import com.audiencecheckout.bff.infrastructure.persistence.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataCartRepository
        extends JpaRepository<CartEntity, UUID> {
}