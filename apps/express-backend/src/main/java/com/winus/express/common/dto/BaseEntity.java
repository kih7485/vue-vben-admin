package com.winus.express.common.dto;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Base Entity for common fields
 */
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Column(name = "CREATE_BY", length = 50)
    private String createBy;

    @CreatedDate
    @Column(name = "CREATE_TIME", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @Column(name = "UPDATE_BY", length = 50)
    private String updateBy;

    @LastModifiedDate
    @Column(name = "UPDATE_TIME")
    private LocalDateTime updateTime;

    @Column(name = "REMARK", length = 500)
    private String remark;
}