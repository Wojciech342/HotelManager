package pl.wojtek.project.exception;

public class ResourceNotFoundException extends RuntimeException {
    String resourceName;
    String field;
    Long fieldId;

    public ResourceNotFoundException() {}

    public ResourceNotFoundException(String resourceName, String field, Long fieldId) {
        super(resourceName + " with " + field + " " + fieldId + " not found");
        this.resourceName = resourceName;
        this.field = field;
        this.fieldId = fieldId;
    }
}