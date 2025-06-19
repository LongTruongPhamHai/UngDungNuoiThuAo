package model;

public class ModelClass {
    private String id;
    private String name;
    private int value;

    public ModelClass() {
    }

    public ModelClass(String id, String name, int value) {
        this.id = id;
        this.name = name;
        this.value = value;
    }

    // Getter & Setter

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
