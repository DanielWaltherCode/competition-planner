export function getFormattedDate(date) {
    if (date === null ||date === undefined) {return ""}
    const dateObject = new Date(date)
    const year = dateObject.getUTCFullYear();
    let month = dateObject.getUTCMonth() + 1;
    let day = dateObject.getUTCDate();

    return year + "-" + getDisplayTime(month) + "-" + getDisplayTime(day);
}

export function getHoursMinutes(date) {
    if (date === null) {return ""}
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
    } else if (match.firstPlayer.length === 2) {
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
    } else if (match.firstPlayer.length === 2) {
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
function getFormattedPlayerName(player) {
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
function getFormattedPlayerNameWithClub(player) {
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

/**
 * In case the category name belongs to one of the default categories we can translate it. Otherwise, we just returned
 * the given name as it is custom category, and we cannot provide a translation.
 * @param name Category name
 * @returns {*} Translated category name if it is default category.
 */
export function tryTranslateCategoryName(name) {
    let key = name
    let translatedDefaultCategories = this.$t("categories.default")
    if (key in translatedDefaultCategories) {
        return translatedDefaultCategories[key]
    }
    return name
}

export function generalErrorHandler(error) {
    if (error.errorType !== null && error.errorType !== undefined) {
        this.$toasted.error(this.$tc("errors." + error.errorType)).goAway(5000)
    }
    else {
        this.$toasted.error(this.$tc("toasts.error.general.standard")).goAway(5000)
    }
}