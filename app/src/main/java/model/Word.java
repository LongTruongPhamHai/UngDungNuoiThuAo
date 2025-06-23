package model;

public class Word {
    private String word;
    private String pos;
    private String def;

    public Word() {}

    public Word(String word, String pos, String def) {
        this.word = word;
        this.pos = pos;
        this.def = def;
    }

    public String getWord() { return word; }
    public String getPos() { return pos; }
    public String getDef() { return def; }

    public void setWord(String word) { this.word = word; }
    public void setPos(String pos) { this.pos = pos; }
    public void setDef(String def) { this.def = def; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Word word1 = (Word) o;
        return word != null ? word.equals(word1.word) : word1.word == null;
    }

    @Override
    public int hashCode() {
        return word != null ? word.hashCode() : 0;
    }
}
