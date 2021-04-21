package com.brodos.reservation.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@DiscriminatorValue("SerialNumberImportTicketRef")
public class SerialNumberImportTicketReference extends TicketReference implements Serializable {
    private static final long serialVersionUID = 4040922068453045365L;

    @Column(name = "import_status")
    @Enumerated(EnumType.STRING)
    private OpenCaseStatus openCaseStatus;

    public SerialNumberImportTicketReference() {
    }

    public SerialNumberImportTicketReference(Owner owner, Article article) {
        this.owner = owner;
        this.article = article;
    }

    public OpenCaseStatus getOpenCaseStatus() {
        return openCaseStatus;
    }

    public void setOpenCaseStatus(OpenCaseStatus openCaseStatus) {
        this.openCaseStatus = openCaseStatus;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
