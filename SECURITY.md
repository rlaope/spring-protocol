# Security Policy

## Supported Versions

| Version | Supported          |
|---------|--------------------|
| 0.1.x   | :white_check_mark: |

## Reporting a Vulnerability

If you discover a security vulnerability in Spring Protocol, please report it responsibly.

**Do NOT open a public GitHub issue for security vulnerabilities.**

Instead, please send an email to **piyrw9754@gmail.com** with:

- A description of the vulnerability
- Steps to reproduce the issue
- Potential impact assessment
- Any suggested fixes (optional)

We will acknowledge receipt within 48 hours and provide a detailed response within 7 days.

## Security Best Practices

When using Spring Protocol:

- Always use TLS for gRPC channels in production
- Do not expose gRPC service addresses in public configuration
- Regularly update dependencies to patch known vulnerabilities
- Use authentication interceptors for sensitive services
