package App.JsonUtility;

public class JsonSaverImporter {

//    /**
//     * Saves filters in a Json file.
//     */
//    @FXML
//    private void saveToJson(String name) throws IOException {
//        ArrayList<Filter> filters = new ArrayList<>(tableFilters.getItems());
//        Gson gson = new GsonBuilder()
//                .registerTypeAdapter(Filter.class, interfaceSerializer(SimpleFilter.class))
//                .registerTypeAdapter(FilterGroup.class, interfaceSerializer(SimpleFilterGroup.class))
//                .create();
//        File file = new File("src/main/resources/file.json");
//        if (file.length() != 0)
//            readJsonFile(gson);
//        writeJsonFile(gson,filters,name);
//    }
//
//    /**
//     * Reads an arrayList of filters in the Json File.
//     *
//     * @param gson gson instance
//     */
//    private void readJsonFile(Gson gson) throws IOException {
//        ArrayList<FilterGroup> fromJson = getListFromJson(gson);
//        if(fromJson != null) {
//            if (!filterGroups.toString().equals(fromJson.toString()))
//                filterGroups.addAll(fromJson);
//        }
//    }
//
//    /**
//     * Takes filters from Json file
//     *
//     * @param gson gson instance
//     * @return     arrayList of filters
//     */
//    private ArrayList<FilterGroup> getListFromJson(Gson gson) throws IOException {
//        Reader reader = Files.newBufferedReader(Paths.get("src/main/resources/file.json"));
//        Type filterListType = new TypeToken<ArrayList<FilterGroup>>() {}.getType();
//        ArrayList<FilterGroup> fromJson = gson.fromJson(reader,filterListType);
//        reader.close();
//        return fromJson;
//    }
//
//    /**
//     * Writes filters in a Json file.
//     *
//     * @param gson  gson instance
//     * @param filters filters to write
//     */
//    private void writeJsonFile(Gson gson, ArrayList<Filter> filters, String name) throws IOException {
//        DialogBuilder d = new DialogBuilder(mainController.getTheme());
//        Writer writer = Files.newBufferedWriter(Paths.get("src/main/resources/file.json"));
//        FilterGroup filterGroup = new SimpleFilterGroup(name, filters);
//        boolean filterGroupPresent = filterGroups.stream().anyMatch(f -> f.equals(filterGroup));
//        boolean filtersPresent = filterGroups.stream().anyMatch(f -> f.getFilters().equals(filterGroup.getFilters()));
//        boolean namePresent = filterGroups.stream().anyMatch(f -> f.getName().equals(name));
//        if(!(filtersPresent || namePresent || filterGroupPresent)) {
//            filterGroups.add(filterGroup);
//            d.info(name + " filter saved with success!");
//        } else if(filterGroupPresent)
//            d.warning("Name and filters already present!");
//        else if(namePresent)
//            d.warning("Name already present!");
//        else d.warning("Filters already present!");
//        gson.toJson(filterGroups, writer);
//        writer.close();
//    }
//
//    /**
//     * Loads filters take from Json file on table and graph.
//     *
//     */
//    @FXML
//    private void loadFilters(String name) throws IOException {
//        tableFilters.getItems().clear();
//        DialogBuilder d = new DialogBuilder(mainController.getTheme());
//        if(graphController.getCsvRead()){
//            Gson gson = new GsonBuilder()
//                    .registerTypeAdapter(Filter.class, interfaceSerializer(SimpleFilter.class))
//                    .registerTypeAdapter(FilterGroup.class, interfaceSerializer(SimpleFilterGroup.class))
//                    .create();
//            ArrayList<FilterGroup> fromJson = getListFromJson(gson);
//            if(fromJson != null){
//                Optional<FilterGroup> filterGroup = fromJson.stream().filter(f -> f.getName().equals(name)).findFirst();
//                if(filterGroup.isPresent()) {
//                    tableFilters.getItems().addAll(filterGroup.get().getFilters());
//                    setCellValueFactory();
//                    tableFilters.getItems().forEach(this::checkFilter);
//                } else
//                    d.warning("Filters not found");
//            } else
//                d.warning("Filters not found");
//        }
//    }

}
