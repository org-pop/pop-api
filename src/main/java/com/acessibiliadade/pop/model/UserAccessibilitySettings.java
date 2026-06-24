package com.acessibiliadade.pop.model;

import com.acessibiliadade.pop.enums.AccessibilityProfile;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_accessibility_settings")
@Data
@NoArgsConstructor
public class UserAccessibilitySettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_accessibility_profiles",
            joinColumns = @JoinColumn(name = "settings_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "profile")
    private Set<AccessibilityProfile> profiles = new HashSet<>();

    @Column(name = "preferred_language", length = 10)
    private String preferredLanguage = "pt-BR";

    @Column(name = "simplified_language")
    private Boolean simplifiedLanguage = false;

    @Column(name = "screen_reader_mode")
    private Boolean screenReaderMode = false;

    @Column(name = "font_size_preference", length = 20)
    private String fontSizePreference = "normal";

    @Column(name = "color_theme", length = 20)
    private String colorTheme = "default";

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
