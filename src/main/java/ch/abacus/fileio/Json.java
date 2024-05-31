/*
 * Json.java
 *
 * Creator:
 * 16.04.2024 09:46 josia.schweizer
 *
 * Maintainer:
 * 16.04.2024 09:46 josia.schweizer
 *
 * Last Modification:
 * $Id:$
 *
 * Copyright (c) 2024 ABACUS Research AG, All Rights Reserved
 */
package ch.abacus.fileio;

import ch.abacus.common.FileIOConst;
import ch.abacus.fileio.components.XMLWRITE;
import ch.abacus.db.entity.Person;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class Json implements PersonIO {

  private PersonIO next;
  GetFileExtension getFileExtension = new GetFileExtension();

  @Override
  public void write(List<Person> persons, File file, XMLWRITE xmlwriteMethode) throws IOException {
    Optional<String> fileExtension = getFileExtension.getExtensionByStringHandling(file.toString());

    if (fileExtension.isPresent() && FileIOConst.JSON.equalsIgnoreCase(fileExtension.get())) {
      try (FileWriter writer = new FileWriter(file)) {
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .setPrettyPrinting()
            .create();
        gson.toJson(persons, writer);
      }
    } else if (getNext() == null) {
      System.out.println(FileIOConst.DOCUMENTNOTSUPPORTED1 + fileExtension.get() + FileIOConst.DOCUMENTNOTSUPPORTED2);
    }
  }

  @Override
  public List<Person> read(File file, XMLWRITE xmlwriteMethode) throws IOException {
    List<Person> newPersons = null;
    Optional<String> fileExtension = getFileExtension.getExtensionByStringHandling(file.toString());

    if (fileExtension.isPresent() && FileIOConst.JSON.equalsIgnoreCase(fileExtension.get())) {
      try (FileReader reader = new FileReader(file)) {
        Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
            .create();
        Type personListType = new TypeToken<List<Person>>() {
        }.getType();
        newPersons = gson.fromJson(reader, personListType);
      }
    } else if (getNext() != null) {
      System.out.println(FileIOConst.DOCUMENTNOTSUPPORTED1 + fileExtension.get() + FileIOConst.DOCUMENTNOTSUPPORTED2);
    }
    return newPersons;
  }

  @Override
  public void setNext(PersonIO next) {
    this.next = next;
  }

  private PersonIO getNext() {
    return next;
  }

  private static class LocalDateAdapter implements JsonSerializer<LocalDate> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FileIOConst.DATETIMEORMATTER);

    @Override
    public JsonElement serialize(LocalDate date, Type type, JsonSerializationContext jsonSerializationContext) {
      return new JsonPrimitive(formatter.format(date));
    }
  }

  private static class LocalDateDeserializer implements JsonDeserializer<LocalDate> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FileIOConst.DATETIMEORMATTER);

    @Override
    public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      String dateString = json.getAsString();
      return LocalDate.parse(dateString, formatter);
    }
  }
}