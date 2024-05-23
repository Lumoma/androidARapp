package com.example.abgabe.data.remote

//CatApi.kt: Ein Interface, das die Endpunkte der API definiert. Es verwendet Retrofit-Annotationen, um die HTTP-Methoden (GET, POST usw.) und die Endpunkt-Pfade anzugeben.

interface CatApi {
    //Die Funktion getCatList() gibt eine Liste von Katzenbildern zurück. Sie verwendet die @GET-Annotation, um den Endpunkt-Pfad anzugeben.
    //Die Funktion gibt ein Call-Objekt zurück, das die Antwort der API enthält.

    //fun getCatList(): Call<List<CatImage>>
}

