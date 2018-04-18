package tdz

// Translated from Google's Java quickstart boilerplate.
// (This is a bit of a mess, but it gets the job done.)

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.json.JsonFactory
import com.google.api.client.util.store.FileDataStoreFactory

import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.*

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.Arrays

object GoogleCalendar {
  val APPLICATION_NAME = "tdz"

  // Directory to store user credentials for this application.
  val credsPath = ".credentials/tdz-google-calendar"

  fun homeFile(path: String): File {
    return File(System.getProperty("user.home"), path)
  }

  fun credsFile(path: String): File {
    return File("${System.getProperty("user.home")}/${credsPath}", path)
  }

  val DATA_STORE_DIR = homeFile(credsPath)

  val DATA_STORE_FACTORY: FileDataStoreFactory = FileDataStoreFactory(DATA_STORE_DIR)
  val JSON_FACTORY: JsonFactory = JacksonFactory.getDefaultInstance()
  val HTTP_TRANSPORT: HttpTransport = GoogleNetHttpTransport.newTrustedTransport()

  val SCOPES: List<String> = listOf(CalendarScopes.CALENDAR)

  fun authorize(): Credential {
    // Load client secrets.
    val secretFile = credsFile("client_secret.json")
    val inputStream: InputStream = FileInputStream(secretFile)
    val clientSecrets: GoogleClientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(inputStream))

    // Build flow and trigger user authorization request.
    val flow: GoogleAuthorizationCodeFlow = GoogleAuthorizationCodeFlow.Builder(
        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
        .setDataStoreFactory(DATA_STORE_FACTORY)
        .setAccessType("offline")
        .build()

    val credential: Credential = AuthorizationCodeInstalledApp(
      flow, LocalServerReceiver()
    ).authorize("user")

    return credential
  }

  fun getService(): com.google.api.services.calendar.Calendar {
    val credential = authorize()
    return com.google.api.services.calendar.Calendar.Builder(
        HTTP_TRANSPORT, JSON_FACTORY, credential)
        .setApplicationName(APPLICATION_NAME)
        .build()
  }
}

private val service = GoogleCalendar.getService()

fun calendars(): List<CalendarListEntry> {
  var results: List<CalendarListEntry> = emptyList()
  var pageToken: String? = null

  do {
    results += service.calendarList()
                      .list()
                      .setPageToken(pageToken)
                      .execute()
                      .getItems()
  } while (pageToken != null)

  return results
}

fun CalendarListEntry.events(): List<Event> {
  var results: List<Event> = emptyList()
  var pageToken: String? = null

  do {
    results += service.events()
                      .list(this.getId())
                      .setPageToken(pageToken)
                      .execute()
                      .getItems()
  } while (pageToken != null)

  return results
}

// Finds and returns a calendar available to the user called "tdz." If none is
// found, creates one and returns it.
fun tdzCalendar(): CalendarListEntry {
  val existingCalendar = calendars().find({it.getSummary() == "tdz"})
  return existingCalendar ?: createTdzCalendar()
}

fun createTdzCalendar(): CalendarListEntry {
  val calendar = Calendar().setSummary("tdz")
  val calendarId = service.calendars()
                          .insert(calendar)
                          .execute()
                          .getId()
  return service.CalendarList().get(calendarId).execute()
}
