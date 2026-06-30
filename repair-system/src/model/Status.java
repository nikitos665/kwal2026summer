package model;

public enum Status {
    NEW("Новая"),
    ACCEPTED("Принята"),
    DIAGNOSTICS("Диагностика"),
    IN_PROGRESS("В ремонте"),
    WAITING_PARTS("Ожидание запчастей"),
    READY("Готова к выдаче"),
    COMPLETED("Выдана"),
    CANCELLED("Отменена");

    private final String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}