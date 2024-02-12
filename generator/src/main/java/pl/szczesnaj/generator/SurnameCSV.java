package pl.szczesnaj.generator;

record SurnameCSV(String surname, int number) implements CSV {

    @Override
    public String getAttribute() {
        return surname;
    }
}
