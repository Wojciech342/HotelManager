package pl.wojtek.project.exception;

import lombok.Getter;

@Getter
public class ResourceAlreadyExistsException extends RuntimeException {
    String resourceName;
    String field;
    Long fieldId;
    String fieldValue;


    public ResourceAlreadyExistsException(String resourceName, String field, Long fieldId) {
        super(resourceName + " with " + field + " " + fieldId + " already exists");
        this.resourceName = resourceName;
        this.field = field;
        this.fieldId = fieldId;
    }

    public ResourceAlreadyExistsException(String resourceName, String field, String fieldValue) {
        super(resourceName + " with " + field + " " + fieldValue + " already exists");
        this.resourceName = resourceName;
        this.field = field;
        this.fieldValue = fieldValue;
    }
}