<h1 align="center">🌱 Automated Watering System</h1>

<p align="center">
  Sistem Penyiraman Tanaman Otomatis Berbasis IoT <br>
  Kontrol pintar melalui aplikasi Android
</p>

---

## Deskripsi Aplikasi

**Automated Watering System** adalah sistem penyiraman tanaman otomatis berbasis **IoT** yang dapat dikontrol melalui **aplikasi Android**.  
Aplikasi ini dirancang untuk membantu pengguna (gardener) dalam mengelola penyiraman tanaman agar lebih **efisien, tepat waktu, dan hemat air**.

Sistem bekerja dengan membaca data **sensor kelembaban tanah** dan **sensor suhu & kelembapan udara**, kemudian menentukan kapan pompa air harus menyala, baik secara **otomatis**, **terjadwal**, maupun **manual** melalui aplikasi.

---

## Diagram Detail Alur Sistem

<p align="center">
  <img src="flow.jpeg" width="90%">
</p>

---

## Fitur Utama Role Pengguna

- **Login & Register**
- **Sensor Monitoring**
  - Kelembaban tanah
  - Suhu & kelembapan udara
- **Mode Otomatis**
  - Penyiraman berdasarkan nilai sensor kelembaban tanah <500
- **Mode Manual**
  - Pengguna dapat menyalakan pompa langsung dari aplikasi
- **Mode Time / Jadwal**
  - Penyiraman otomatis berdasarkan jam yang ditentukan
- **Mode Cycle**
  - Penyiraman berkala sesuai interval waktu tertentu
- **Notifikasi**
  - Peringatan jika tanaman dalam kondisi kering, penyiraman aktif dan selesai
- **Activity History**
  - Log waktu penyiraman dan aktivitas
- **System Report**
  - Lapor bug ke admin
---

## Fitur Utama Role Admin

- **Manage Access**
  - Tambah Akun
  - Update dan Delete Akun
- **User History**
  - Menampilkan riwayat aktivitas user
- **System Report**
  - Menampilkan report bug dari user dan merubah status report
- **Mode Manual**
  - Menyalakan pompa langsung dari aplikasi
- **Mode Otomatis**
  - Penyiraman berdasarkan nilai sensor kelembaban tanah < 500
- **Mode Time / Jadwal**
  - Penyiraman otomatis berdasarkan jam yang ditentukan
- **Mode Cycle**
  - Penyiraman berkala sesuai interval waktu tertentu
- **Notifikasi**
  - Peringatan jika tanaman dalam kondisi kering, penyiraman aktif dan selesai
---

## 🖼️ Tampilan Aplikasi (Role User)

### Login & Register
<table align="center">
  <tr>
    <td align="center">
      <img src="assets/screenshots/login.jpg" width="240"/><br/>
      <b>Login</b>
    </td>
    <td align="center">
      <img src="assets/screenshots/register.jpg" width="240"/><br/>
      <b>Register</b>
    </td>
  </tr>
</table>

---

### Dashboard & Status Sistem
<p align="center">
  <img src="assets/screenshots/dashboard.jpg" width="240"/><br/>
  <b>Dashboard</b>
</p>

---

### Sensor Monitoring
<p align="center">
  <img src="assets/screenshots/sensormonitoring.jpg" width="240"/><br/>
  <b>Sensor Monitoring</b>
</p>

---

### Mode Penyiraman
<p align="center">
  <img src="assets/screenshots/mode.jpg" width="240"/><br/>
  <b>Mode Penyiraman</b>
</p>

---

### Notifikasi
<table align="center">
  <tr>
    <td align="center">
      <img src="assets/screenshots/notification.jpg" width="240"/><br/>
      <b>Notification</b>
    </td>
    <td align="center">
      <img src="assets/screenshots/notifbar.jpg" width="240"/><br/>
      <b>Notification Bar</b>
    </td>
  </tr>
</table>

---

### Activity History
<p align="center">
  <img src="assets/screenshots/activityhistory.jpg" width="240"/><br/>
  <b>Activity History</b>
</p>

---

### Report
<p align="center">
  <img src="assets/screenshots/report.jpg" width="240"/><br/>
  <b>System Report</b>
</p>

---

### Settings
<p align="center">
  <img src="assets/screenshots/settings.jpg" width="240"/><br/>
  <b>Settings</b>
</p>

## 🖼️ Tampilan Aplikasi (Role Admin)

### Manajemen Akun
<table align="center">
  <tr>
    <td align="center">
      <img src="assets/screenshots/addaccount.jpg" width="240"/><br/>
      <b>Add Account</b>
    </td>
    <td align="center">
      <img src="assets/screenshots/editaccount.jpg" width="240"/><br/>
      <b>Edit Account</b>
    </td>
  </tr>
</table>

---

### Manage Access
<p align="center">
  <img src="assets/screenshots/manageaccess.jpg" width="240"/><br/>
  <b>Manage Access</b>
</p>

---

### Profile Admin
<p align="center">
  <img src="assets/screenshots/profileadmin.jpg" width="240"/><br/>
  <b>Profile Admin</b>
</p>

---

### Report dari User
<table align="center">
  <tr>
    <td align="center">
      <img src="assets/screenshots/reportfromuser.jpg" width="240"/><br/>
      <b>Report from User</b>
    </td>
    <td align="center">
      <img src="assets/screenshots/statusreport.jpg" width="240"/><br/>
      <b>Status Report</b>
    </td>
  </tr>
</table>

---

### User Activity History
<p align="center">
  <img src="assets/screenshots/userhistory.jpg" width="240"/><br/>
  <b>User History</b>
</p>

---

## Cara Kerja Sistem

1. ESP8266 membaca data:
   - Sensor kelembaban tanah
   - Sensor suhu & kelembapan (DHT22)
2. Data dikirim ke Firebase
3. Aplikasi membaca data secara **real-time**
4. Sistem menentukan aksi penyiraman berdasarkan:
   - Mode aktif (Auto / Manual / Time / Cycle)
   - Nilai sensor
5. Pompa air diaktifkan melalui **relay**
6. Status pompa dan log penyiraman disimpan ke Firebase untuk di Tampilkan pada Aplikasi

---

## Teknologi yang Digunakan

- **Hardware**
  - ESP8266
  - Soil Moisture Sensor
  - DHT22
  - Relay Module
  - Pompa Air

- **Software**
  - Android (Native) - Java
  - Firebase
  - Arduino IDE
  - IoT Communication (WiFi)
---


<p align="center">
  🌿 <i>Smart watering for healthier plants</i> 🌿
</p>
