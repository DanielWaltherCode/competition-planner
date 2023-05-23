import {$t, i18n} from "@/i18n";

export function getFormattedDate(date) {
    if (date === null || date === undefined || date === "") {return ""}
    const dateObject = new Date(date)
    const year = dateObject.getUTCFullYear();
    let month = dateObject.getUTCMonth() + 1;
    let day = dateObject.getUTCDate();

    return year + "-" + getDisplayTime(month) + "-" + getDisplayTime(day);
}

export function getHoursMinutes(date) {
    if (date === null || date === undefined || date === "") {return ""}
    const dateObject = new Date(date)
    return getDisplayTime(dateObject.getHours()) + ":" + getDisplayTime(dateObject.getMinutes())
}

export function getPlayerOneWithClub(match) {
    let playerOne = ""
    if (match.firstPlayer.length === 1) {
        playerOne = getFormattedPlayerNameWithClub(match.firstPlayer[0])
    } else if (match.firstPlayer.length === 2) {
        playerOne = getFormattedPlayerNameWithClub(match.firstPlayer[0]) + " / " + getFormattedPlayerNameWithClub(match.firstPlayer[1])
    }
    return playerOne
}

export function getPlayerTwoWithClub(match) {
    let playerTwo = ""
    if (match.secondPlayer.length === 1) {
        playerTwo = getFormattedPlayerNameWithClub(match.secondPlayer[0])
    } else if (match.secondPlayer.length === 2) {
        playerTwo = getFormattedPlayerNameWithClub(match.secondPlayer[0]) + " / " + getFormattedPlayerNameWithClub(match.secondPlayer[1])
    }
    return playerTwo
}

export function getPlayerOne(match) {
    let playerOne = ""
    if (match.firstPlayer.length === 1) {
        playerOne = getFormattedPlayerName(match.firstPlayer[0])
    } else if (match.firstPlayer.length === 2) {
        playerOne = getFormattedPlayerName(match.firstPlayer[0]) + " / " + getFormattedPlayerName(match.firstPlayer[1])
    }
    return playerOne
}

export function getPlayerTwo(match) {
    let playerTwo = ""
    if (match.secondPlayer.length === 1) {
        playerTwo = getFormattedPlayerName(match.secondPlayer[0])
    } else if (match.secondPlayer.length === 2) {
        playerTwo = getFormattedPlayerName(match.secondPlayer[0]) + " / " + getFormattedPlayerName(match.secondPlayer[1])
    }
    return playerTwo
}

/**
 * Returns the name of player formatted like "Firstname Lastname". In case the player is a place holder player, only
 * the "Firstname" is returned, which can be either Placeholder or a group position like A1.
 *
 * @param player
 * @returns {string|null|default.methods.firstName|*}
 */
export function getFormattedPlayerName(player) {
    if (player.id === -1) { // This is a Placeholder, strip it of last name
        return player.firstName
    } else {
        return player.firstName + " " + player.lastName
    }
}

/**
 * Returns the name of player formatted like "Firstname Lastname Club". In case the player is a place holder player, only
 * the "Firstname" is returned, which can be either Placeholder or a group position like A1.
 *
 * @param player
 * @returns {string|null|default.methods.firstName|*}
 */
export function getFormattedPlayerNameWithClub(player) {
    if (player.id === -1) { // This is a Placeholder, strip it of last name and club.
        return player.firstName
    } else {
        return player.firstName + " " + player.lastName + " " + player.club.name
    }
}

export function isPlayerOneWinner(match) {
    if (match.winner.length === 0) {
        return false
    }
    let winnerIds = match.winner.map(player => player.id)

    return winnerIds.includes(match.firstPlayer[0].id)
}

export function isPlayerTwoWinner(match) {
    if (match.winner.length === 0) {
        return false
    }
    let winnerIds = match.winner.map(player => player.id)

    return winnerIds.includes(match.secondPlayer[0].id)

}

export function getClub(playerDTOs) {
    if (playerDTOs.length === 1) {
        return playerDTOs[0].club.name
    }
    else if (playerDTOs.length === 2) {
        return playerDTOs[0].club.name + "/" + playerDTOs[1].club.name
    }
}

export function getDisplayTime(time) {
    if (time < 10) {
        time = "0" + time;
    }
    return time;
}

export function didPlayerOneGiveWO(match) {
    if (!match.wasWalkover) {
        return false;
    }
    if (match.winner.length === 0) {
        return false
    }
    let winnerIds = match.winner.map(player => player.id)

    return winnerIds.includes(match.secondPlayer[0].id)
}

export function didPlayerTwoGiveWO(match) {
    if (!match.wasWalkover) {
        return false;
    }
    if (match.winner.length === 0) {
        return false
    }
    let winnerIds = match.winner.map(player => player.id)

    return winnerIds.includes(match.firstPlayer[0].id)
}

export function undefinedOrNull(object) {
    return object === null || object === undefined
}

export function undefinedOrNullOrEmpty(object) {
    return object === "" || undefinedOrNull(object)
}

/**
 * In case the category name belongs to one of the default categories we can translate it. Otherwise, we just returned
 * the given name as it is custom category, and we cannot provide a translation.
 * @param name Category name
 * @returns {*} Translated category name if it is default category.
 */
export function tryTranslateCategoryName(name) {
    let key = name
    let translatedDefaultCategories = categories[i18n.global.locale]
    if (key in translatedDefaultCategories) {
        return translatedDefaultCategories[key]
    }
    return name
}

export function generalErrorHandler(error) {
    if (error === undefined) {
        this.$toasted.error(this.$tc("toasts.error.general.standard")).goAway(5000)
    }
    if (error.errorType !== null && error.errorType !== undefined) {
        this.$toasted.error(this.$tc("errors." + error.errorType)).goAway(8000)
    }
    else {
        this.$toasted.error(this.$tc("toasts.error.general.standard")).goAway(5000)
    }
}

export function shouldShowPlayoff(playoff) {
    if (playoff === null || playoff.length === 0) {
        return false;
    }
    let nonPlaceholderPlayers = 0
    playoff[0].matches.forEach(match => {
        const firstPlayerId = match.firstPlayer[0].id
        const secondPlayerId = match.secondPlayer[0].id
        if ((firstPlayerId !== -1) && (secondPlayerId !== -1)) {
            nonPlaceholderPlayers += 2
        }
    })
    return nonPlaceholderPlayers === playoff[0].matches.length * 2;
}

const categories = {
    "sv": {
        "MEN_1": "Herrar 1",
        "MEN_2": "Herrar 2",
        "MEN_3": "Herrar 3",
        "MEN_4": "Herrar 4",
        "MEN_5": "Herrar 5",
        "MEN_6": "Herrar 6",
        "WOMEN_1": "Damer 1",
        "WOMEN_2": "Damer 2",
        "WOMEN_3": "Damer 3",
        "WOMEN_4": "Damer 4",
        "WOMEN_JUNIOR_17": "Damjuniorer 17",
        "GIRLS_15": "Flickor 15",
        "GIRLS_14": "Flickor 14",
        "GIRLS_13": "Flickor 13",
        "GIRLS_12": "Flickor 12",
        "GIRLS_11": "Flickor 11",
        "GIRLS_10": "Flickor 10",
        "GIRLS_9": "Flickor 9",
        "GIRLS_8": "Flickor 8",
        "MEN_JUNIOR_17": "Herrjuniorer 17",
        "BOYS_15": "Pojkar 15",
        "BOYS_14": "Pojkar 14",
        "BOYS_13": "Pojkar 13",
        "BOYS_12": "Pojkar 12",
        "BOYS_11": "Pojkar 11",
        "BOYS_10": "Pojkar 10",
        "BOYS_9": "Pojkar 9",
        "BOYS_8": "Pojkar 8",
        "MEN_TEAMS": "Herrdubbel",
        "WOMEN_TEAMS": "Damdubbel"
    },
    "en": {
        "MEN_1": "Men 1",
        "MEN_2": "Men 2",
        "MEN_3": "Men 3",
        "MEN_4": "Men 4",
        "MEN_5": "Men 5",
        "MEN_6": "Men 6",
        "WOMEN_1": "Women 1",
        "WOMEN_2": "Women 2",
        "WOMEN_3": "Women 3",
        "WOMEN_4": "Women 4",
        "WOMEN_JUNIOR_17": "Women Juniors 17",
        "GIRLS_15": "Girls 15",
        "GIRLS_14": "Girls 14",
        "GIRLS_13": "Girls 13",
        "GIRLS_12": "Girls 12",
        "GIRLS_11": "Girls 11",
        "GIRLS_10": "Girls 10",
        "GIRLS_9": "Girls 9",
        "GIRLS_8": "Girls 8",
        "MEN_JUNIOR_17": "Men Juniors 17",
        "BOYS_15": "Boys 15",
        "BOYS_14": "Boys 14",
        "BOYS_13": "Boys 13",
        "BOYS_12": "Boys 12",
        "BOYS_11": "Boys 11",
        "BOYS_10": "Boys 10",
        "BOYS_9": "Boys 9",
        "BOYS_8": "Boys 8",
        "MEN_TEAMS": "Men's doubles",
        "WOMEN_TEAMS": "Women's doubles"
    }
}