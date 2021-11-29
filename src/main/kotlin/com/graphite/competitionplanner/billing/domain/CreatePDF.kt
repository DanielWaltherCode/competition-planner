package com.graphite.competitionplanner.billing.domain

import com.graphite.competitionplanner.competition.domain.FindCompetitions
import org.apache.pdfbox.contentstream.PDContentStream
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.springframework.stereotype.Component
import java.io.ByteArrayOutputStream

@Component
class CreatePDF(
    val getCostSummaryForPlayers: GetCostSummaryForPlayers,
    val getCostSummaryForClub: GetCostSummaryForClub,
    val findCompetitions: FindCompetitions
) {

    fun execute(competitionId: Int, clubId: Int): ByteArray {
        val clubCosts = getCostSummaryForClub.execute(competitionId, clubId)
        val playerCosts = getCostSummaryForPlayers.execute(competitionId, clubId)
        val competition = findCompetitions.byId(competitionId)
        val document = PDDocument()

        // First page
        val firstPage = PDPage()
        val pageContentStream = PDPageContentStream(document, firstPage)
        pageContentStream.beginText()

        // Heading
        pageContentStream.setFont(PDType1Font.TIMES_ROMAN, 16f)
        pageContentStream.newLineAtOffset(20f, 400f)
        pageContentStream.showText("")
        pageContentStream.endText()
        pageContentStream.close()

        // Second page (further info to accompany bill)
        val secondPage = PDPage()

        document.addPage(firstPage)

        // Return as bytearray
        val baos = ByteArrayOutputStream()
        document.save(baos)
        document.close()
        return baos.toByteArray()
    }
}