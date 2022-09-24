function getFormattedDate(date) {
    if (date === null ||date === undefined) {return ""}
    const dateObject = new Date(date)
    const year = dateObject.getUTCFullYear();
    let month = dateObject.getUTCMonth() + 1;
    let day = dateObject.getUTCDate();

    return year + "-" + getDisplayTime(month) + "-" + getDisplayTime(day);
}

function getHoursMinutes(date) {
    if (date === null) {return ""}
    const dateObject = new Date(date)
    return getDisplayTime(dateObject.getHours()) + ":" + getDisplayTime(dateObject.getMinutes())
}

function getPlayerOneWithClub(match) {
    let playerOne = ""
    if (match.firstPlayer.length === 1) {
        playerOne = getFormattedPlayerNameWithClub(match.firstPlayer[0])
    } else if (match.firstPlayer.length === 2) {
        playerOne = getFormattedPlayerNameWithClub(match.firstPlayer[0]) + " / " + getFormattedPlayerNameWithClub(match.firstPlayer[1])
    }
    return playerOne
}

function getPlayerTwoWithClub(match) {
    let playerTwo = ""
    if (match.secondPlayer.length === 1) {
        playerTwo = getFormattedPlayerNameWithClub(match.secondPlayer[0])
    } else if (match.secondPlayer.length === 2) {
        playerTwo = getFormattedPlayerNameWithClub(match.secondPlayer[0]) + " / " + getFormattedPlayerNameWithClub(match.secondPlayer[1])
    }
    return playerTwo
}

function getPlayerOne(match) {
    let playerOne = ""
    if (match.firstPlayer.length === 1) {
        playerOne = getFormattedPlayerName(match.firstPlayer[0])
    } else if (match.firstPlayer.length === 2) {
        playerOne = getFormattedPlayerName(match.firstPlayer[0]) + " / " + getFormattedPlayerName(match.firstPlayer[1])
    }
    return playerOne
}

function getPlayerTwo(match) {
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
 * @param playerList
 * @returns {string|null|default.methods.firstName|*}
 */
function getFormattedPlayerNameWithClub(playerList) {
    let playerNames = ""
    if (playerList.length === 1) {
        playerNames = getFormattedPlayerName(playerList[0])
    } else if (match.firstPlayer.length === 2) {
        playerNames = getFormattedPlayerName(playerList[0]) + " / " + getFormattedPlayerName(playerList[1])
    }
    return playerList
}

function getFormattedPlayerNamesWithClub(player) {
    if (player.id === -1) { // This is a Placeholder, strip it of last name and club.
        return player.firstName
    } else {
        return player.firstName + " " + player.lastName + " " + player.club.name
    }
}

function isPlayerOneWinner(match) {
    if (match.winner.length === 0) {
        return false
    }
    let winnerIds = match.winner.map(player => player.id)

    return winnerIds.includes(match.firstPlayer[0].id)
}

function isPlayerTwoWinner(match) {
    if (match.winner.length === 0) {
        return false
    }
    let winnerIds = match.winner.map(player => player.id)

    return winnerIds.includes(match.secondPlayer[0].id)

}

function getClub(playerDTOs) {
    if (playerDTOs.length === 1) {
        return playerDTOs[0].club.name
    }
    else if (playerDTOs.length === 2) {
        return playerDTOs[0].club.name + "/" + playerDTOs[1].club.name
    }
}

function getDisplayTime(time) {
    if (time < 10) {
        time = "0" + time;
    }
    return time;
}

function didPlayerOneGiveWO(match) {
    if (!match.wasWalkover) {
        return false;
    }
    if (match.winner.length === 0) {
        return false
    }
    let winnerIds = match.winner.map(player => player.id)

    return winnerIds.includes(match.secondPlayer[0].id)
}

function didPlayerTwoGiveWO(match) {
    if (!match.wasWalkover) {
        return false;
    }
    if (match.winner.length === 0) {
        return false
    }
    let winnerIds = match.winner.map(player => player.id)

    return winnerIds.includes(match.firstPlayer[0].id)
}

function undefinedOrNull(object) {
    return object === null || object === undefined
}