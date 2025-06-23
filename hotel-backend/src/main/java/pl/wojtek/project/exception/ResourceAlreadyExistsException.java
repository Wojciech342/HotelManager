package pl.wojtek.project.exception;

public class ResourceAlreadyTakenException extends RuntimeException {
    String resourceName;
    String field;
    Long fieldId;
    String fieldValue;

    public ResourceAlreadyTakenException() {}

    public ResourceAlreadyTakenException(String resourceName, String field, Long fieldId) {
        super(resourceName + " with " + field + " " + fieldId + " not found");
        this.resourceName = resourceName;
        this.field = field;
        this.fieldId = fieldId;
    }

    public ResourceAlreadyTakenException(String resourceName, String field, String fieldValue) {
        super(resourceName + " with " + field + " " + fieldValue + " not found");
        this.resourceName = resourceName;
        this.field = field;
        this.fieldValue = fieldValue;
    }
}
}
