package pl.szczesnaj.generator;

record NameCSV(String name,String gender,int number) implements CSV {

    @Override
    public String getAttribute() {
        return name;
    }
}
