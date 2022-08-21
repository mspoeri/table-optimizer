package de.p58i.utils.tableoptimizer.config

import org.jline.utils.AttributedStyle
import org.jline.utils.AttributedString
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.shell.jline.PromptProvider


@Configuration
data class ShellConfig(
    val shellPrompt: String = "t-opt:>"
) {
    @Bean
    fun getPromptProvider() : PromptProvider {
        return PromptProvider {
            AttributedString(
                shellPrompt, AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW)
            )
        }
    }
}
