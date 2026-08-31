import { defineConfig } from 'vitepress'

export default defineConfig({
  lang: 'de-DE',
  title: '3LGM²-Tool',
  description: 'Dokumentation des 3LGM²-Tools',
  base: '/3LGM2-Tool/',
  cleanUrls: true,
  assetsInclude: ['**/*.PNG', '**/*.png', '**/*.jpg', '**/*.jpeg', '**/*.gif', '**/*.svg'],
  themeConfig: {
    nav: [
      { text: 'Dokumentation', link: '/01.%20First%20steps' },
      { text: 'FAQ', link: '/FAQ' },
      { text: 'GitHub', link: 'https://github.com/IMISE/3LGM2-Tool' }
    ],
    sidebar: [
      {
        text: 'Grundlagen',
        items: [
          { text: 'Erste Schritte', link: '/01.%20First%20steps' },
          { text: 'Vorwort', link: '/01.%20First%20steps/01.%20Preface' },
          { text: 'Modelle erstellen, öffnen und speichern', link: '/01.%20First%20steps/02.%20Create,%20open%20and%20save%20models' },
          { text: 'Im Modell navigieren', link: '/01.%20First%20steps/03.%20Navigate%20in%20the%20model' },
          { text: 'Elemente hinzufügen', link: '/01.%20First%20steps/04.%20Adding%20elements' }
        ]
      },
      {
        text: '3LGM²-Metamodell',
        items: [
          { text: 'Übersicht', link: '/02.%203LGM%C2%B2%20Metamodel' },
          { text: 'Fachliche Ebene', link: '/02.%203LGM%C2%B2%20Metamodel/01.%20Domain%20Layer' },
          { text: 'Logische Werkzeugebene', link: '/02.%203LGM%C2%B2%20Metamodel/02.%20Logical%20Tool%20Layer' },
          { text: 'Physische Werkzeugebene', link: '/02.%203LGM%C2%B2%20Metamodel/03.%20Physical%20Tool%20Layer' },
          { text: 'Beziehungen zwischen Ebenen', link: '/02.%203LGM%C2%B2%20Metamodel/04.%20Inter-Layer-Relationships' },
          { text: 'Vergröbern und Verfeinern', link: '/02.%203LGM%C2%B2%20Metamodel/05.%20Coarsening%20and%20Refining' }
        ]
      },
      {
        text: '3LGM²-Tool',
        items: [
          { text: 'Übersicht', link: '/03.%20The%203LGM%C2%B2-Tool' },
          { text: 'Oberfläche', link: '/03.%20The%203LGM%C2%B2-Tool/01.%20Overview' },
          { text: 'Eigenschaften der Modellelemente', link: '/03.%20The%203LGM%C2%B2-Tool/02.%20Properties%20of%20the%20model%20elements' },
          { text: 'Matrixansicht', link: '/03.%20The%203LGM%C2%B2-Tool/03.%20The%20Matrix-View' },
          { text: 'Modellanalyse', link: '/03.%20The%203LGM%C2%B2-Tool/04.%20Analysis%20of%203LGM%C2%B2%20models' },
          { text: 'Import und Export', link: '/03.%20The%203LGM%C2%B2-Tool/05.%20Import%20and%20Export%20of%20Models' },
          { text: 'Benutzerfelder und Subtypen', link: '/03.%20The%203LGM%C2%B2-Tool/06.%20Userfields%20and%20Subtypes' },
          { text: 'Tastenkürzel', link: '/03.%20The%203LGM%C2%B2-Tool/07.%20Keyboard%20Shortcuts' },
          { text: 'Vorlagen-Browser', link: '/03.%20The%203LGM%C2%B2-Tool/08.%20Template-Browser' }
        ]
      },
      {
        text: '3LGM² und IHE',
        items: [
          { text: 'IHE: Einführung', link: '/IHE/1.%20Introduction' },
          { text: 'IHE: Actors verwenden', link: '/IHE/Usage%20of%20IHE%20Actors' },
          { text: 'IHE: Actor-Abhängigkeiten', link: '/IHE/MusBeGroupedWith' },
          { text: 'IHE: Transactions verwenden', link: '/IHE/Usage%20of%20IHE%20Transactions' }
        ]
      },
      {
        text: 'Weitere Themen',
        items: [
          { text: 'FAQ (Deutsch)', link: '/FAQ' },
          { text: 'FAQ (English)', link: '/FAQ%20-%20English' }
        ]
      }
    ],
    socialLinks: [
      { icon: 'github', link: 'https://github.com/IMISE/3LGM2-Tool' }
    ],
    search: { provider: 'local' }
  }
})
