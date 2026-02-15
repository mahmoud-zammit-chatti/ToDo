import React, { useState } from 'react';
import {
  SafeAreaView,
  StatusBar,
  StyleSheet,
  Text,
  View,
  Pressable,
} from 'react-native';

const MODES = {
  severe: {
    title: 'Severe Protection',
    description: 'Blocks all high-risk & suspicious domains',
  },
  moderate: {
    title: 'Moderate Protection',
    description: 'Balances safety and browsing flexibility',
  },
};

export default function App() {
  const [selectedMode, setSelectedMode] = useState('severe');

  return (
    <SafeAreaView style={styles.safeArea}>
      <StatusBar barStyle="dark-content" />
      <View style={styles.container}>
        <View style={styles.headerRow}>
          <View>
            <Text style={styles.appName}>SafeBrowse</Text>
            <Text style={styles.subtitle}>Security Companion</Text>
          </View>
          <View style={styles.iconRow}>
            <Text style={styles.smallIcon}>⏱</Text>
            <Text style={styles.smallIcon}>⚙</Text>
          </View>
        </View>

        <View style={styles.counterCard}>
          <Text style={styles.shield}>🛡</Text>
          <Text style={styles.counterValue}>24</Text>
          <Text style={styles.counterLabel}>Threats Blocked</Text>
        </View>

        <Text style={styles.sectionTitle}>Protection Mode</Text>
        {Object.entries(MODES).map(([key, mode]) => {
          const selected = selectedMode === key;
          return (
            <Pressable
              key={key}
              style={[styles.modeCard, selected && styles.modeCardSelected]}
              onPress={() => setSelectedMode(key)}
            >
              <Text style={[styles.modeTitle, selected && styles.modeTitleSelected]}>
                {mode.title}
              </Text>
              <Text
                style={[
                  styles.modeDescription,
                  selected && styles.modeDescriptionSelected,
                ]}
              >
                {mode.description}
              </Text>
            </Pressable>
          );
        })}

        <View style={styles.wordCard}>
          <Text style={styles.wordHeader}>Cybersecurity Word of the Day</Text>
          <Text style={styles.wordTerm}>Phishing</Text>
          <Text style={styles.wordDescription}>
            A social engineering attack where users are tricked into revealing
            sensitive information through fake websites or messages.
          </Text>
        </View>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  safeArea: {
    flex: 1,
    backgroundColor: '#f2f5fa',
  },
  container: {
    flex: 1,
    paddingHorizontal: 20,
    paddingTop: 16,
  },
  headerRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: 20,
  },
  appName: {
    fontSize: 30,
    fontWeight: '700',
    color: '#102647',
  },
  subtitle: {
    marginTop: 2,
    color: '#4f6789',
    fontSize: 14,
  },
  iconRow: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  smallIcon: {
    fontSize: 18,
    color: '#1e3d69',
    marginLeft: 10,
  },
  counterCard: {
    backgroundColor: '#0f2b4c',
    borderRadius: 18,
    paddingVertical: 26,
    marginBottom: 24,
    alignItems: 'center',
    shadowColor: '#0b1f37',
    shadowOffset: { width: 0, height: 8 },
    shadowOpacity: 0.18,
    shadowRadius: 14,
    elevation: 4,
  },
  shield: {
    color: '#9bc5ff',
    fontSize: 24,
    marginBottom: 2,
  },
  counterValue: {
    color: '#ffffff',
    fontSize: 52,
    fontWeight: '700',
    lineHeight: 62,
  },
  counterLabel: {
    color: '#dce6f5',
    fontSize: 16,
    fontWeight: '500',
  },
  sectionTitle: {
    color: '#163862',
    fontSize: 17,
    fontWeight: '600',
    marginBottom: 10,
  },
  modeCard: {
    backgroundColor: '#ffffff',
    borderRadius: 14,
    borderWidth: 1,
    borderColor: '#d4deec',
    padding: 14,
    marginBottom: 10,
  },
  modeCardSelected: {
    borderColor: '#1f4778',
    backgroundColor: '#e9f1ff',
  },
  modeTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: '#1b3558',
    marginBottom: 4,
  },
  modeTitleSelected: {
    color: '#0f2b4c',
  },
  modeDescription: {
    color: '#4f6789',
    fontSize: 13,
  },
  modeDescriptionSelected: {
    color: '#284a75',
  },
  wordCard: {
    marginTop: 'auto',
    marginBottom: 16,
    backgroundColor: '#ffffff',
    borderRadius: 16,
    padding: 16,
    shadowColor: '#17263b',
    shadowOffset: { width: 0, height: 5 },
    shadowOpacity: 0.08,
    shadowRadius: 10,
    elevation: 3,
  },
  wordHeader: {
    fontSize: 15,
    color: '#183b67',
    fontWeight: '600',
    marginBottom: 8,
  },
  wordTerm: {
    fontSize: 20,
    color: '#0f2b4c',
    fontWeight: '700',
    marginBottom: 6,
  },
  wordDescription: {
    fontSize: 13,
    lineHeight: 20,
    color: '#415a7b',
  },
});
