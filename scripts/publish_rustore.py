#!/usr/bin/env python3
"""
RuStore Publisher API — автоматическая публикация приложения.

Запуск:
    pip install requests cryptography pyjwt Pillow
    python3 publish_rustore.py

Скрипт:
  1. Генерирует JWT токен с RSA-256
  2. Создаёт/обновляет карточку приложения
  3. Загружает описание, скриншоты, иконку
  4. НЕ отправляет на модерацию (отправка — отдельная команда)

⚠️  БЕЗОПАСНОСТЬ: после использования замени API ключ в RuStore Console
    https://console.rustore.ru → Настройки → API ключи
"""

import os, sys, time, json, base64, mimetypes
from pathlib import Path

# ── Установка зависимостей ──────────────────────────────────────────
try:
    import jwt, requests
    from cryptography.hazmat.primitives.serialization import load_der_private_key, load_pem_private_key
    from cryptography.hazmat.primitives import serialization
except ImportError:
    print("Устанавливаю зависимости...")
    os.system(f"{sys.executable} -m pip install requests cryptography pyjwt Pillow")
    import jwt, requests
    from cryptography.hazmat.primitives.serialization import load_der_private_key
    from cryptography.hazmat.primitives import serialization

# ═══════════════════════════════════════════════════════════════════════
# КОНФИГУРАЦИЯ — заполни перед запуском
# ═══════════════════════════════════════════════════════════════════════

KEY_ID   = "2351028944"           # ID ключа из RuStore Console
PACKAGE  = "org.nla.videoavataraii"
BASE_URL = "https://public-api.rustore.ru/public/v1"

# Приватный ключ PKCS8 (base64 без заголовков PEM)
# Можно также передать через env: RUSTORE_PRIVATE_KEY=<base64>
PRIVATE_KEY_B64 = os.environ.get("RUSTORE_PRIVATE_KEY", "")  # передай через env: export RUSTORE_PRIVATE_KEY="..."


if __name__ == "__main__":
    main()
