package com.decamanager.transporte;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FechasCoherentesValidator implements ConstraintValidator<FechasCoherentes, TransporteRequest> {
    
    @Override
    public boolean isValid(TransporteRequest request, ConstraintValidatorContext context) {
        if(request == null || request.fechaCarga() == null || request.fechaDescarga() == null) {
            return true; // @NotNull en cada campo ya cubre los nulos
        }
        return !request.fechaCarga().isAfter(request.fechaDescarga());
    }
}
