package com.example.myanimelist

import com.example.myanimelist.di.startAllModules
import com.example.myanimelist.dto.LoadDTO
import com.example.myanimelist.managers.SceneManager
import com.example.myanimelist.models.Anime
import com.example.myanimelist.models.User
import com.example.myanimelist.repositories.animes.IAnimeRepository
import com.example.myanimelist.repositories.users.IUsersRepository
import com.example.myanimelist.service.anime.IAnimeStorage
import com.example.myanimelist.service.txt.TxtBackup
import com.example.myanimelist.utils.Themes
import com.example.myanimelist.utils.ThemesManager
import javafx.application.Application
import javafx.application.Application.launch
import javafx.stage.Stage
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDate
import java.util.*


class MyAnimeListApplication : Application(), KoinComponent {
    private val animeRepository: IAnimeRepository by inject()
    private val userRepository: IUsersRepository by inject()
    private val animeStorage: IAnimeStorage by inject()
    override fun start(stage: Stage) {
        startAllModules()
        initAnimes(animeRepository, animeStorage, userRepository)
        val sceneManager = SceneManager
        sceneManager.setAppClass<MyAnimeListApplication>()
        sceneManager.initSplash(stage)
    }

    companion object {
        fun onExit() {
            val loadDTO = LoadDTO(isLoaded = true, isNightMode = ThemesManager.currentTheme == Themes.OSCURO)
            TxtBackup().save(loadDTO)
        }
    }

}


fun main() {
    launch(MyAnimeListApplication::class.java)
}

fun initAnimes(animeRepository: IAnimeRepository, animeStorage: IAnimeStorage, userRepository: IUsersRepository) {
    val loadData = TxtBackup().load()
    if (loadData?.isLoaded == true) {
        ThemesManager.currentTheme = if (loadData.isNightMode)
            Themes.OSCURO
        else
            Themes.CLARO
        return
    }

    animeRepository.deleteAll()

    initUsers(userRepository)

    val listAnimes =
        animeStorage.load() ?: throw Exception("listanimes is null")
    for (item in listAnimes) {
        animeRepository.add(item.fromDTO())
    }
    TxtBackup().save(
        LoadDTO(isLoaded = true, isNightMode = false)
    )
    return
}

fun initUsers(userRepository: IUsersRepository) {
    userRepository.deleteAll()
    userRepository.addAdmin(
        User(
            "admin",
            "admin@gmail.com",
            "123",
            LocalDate.parse("2015-12-17"),
            LocalDate.parse("2015-12-17"),
            null,
            emptyList<Anime>().toMutableList(),
            UUID.randomUUID(),
            true
        )
    )
}

//checkDataBase(get(_root_ide_package_.com.example.myanimelist.managers.DataBaseManager::class.java))

//}


