package org.example;

public class House {

    private String roof;

    private String wall;

    private Integer numWindows;

    private Integer numGates;

    public House() {}

    @Override
    public String toString() {
        return "House{" +
                "roof='" + roof + '\'' +
                ", wall='" + wall + '\'' +
                ", numWindows=" + numWindows +
                ", numGates=" + numGates +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private House house;

        public Builder() {
            house = new House();
        }

        public Builder setRoof(String roof) {
            house.roof = roof;
            return this;
        }

        public Builder setWall(String wall) {
            house.wall = wall;
            return this;
        }

        public Builder setNumWindows(Integer numWindows) {
            house.numWindows = numWindows;
            return this;
        }

        public Builder setNumGates(Integer numGates) {
            house.numGates = numGates;
            return this;
        }

        public House build() {
            return house;
        }

    }
}
