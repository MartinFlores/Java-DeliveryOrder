[Setup]
AppName=DeliveryOrder POS
AppVersion=1.0
AppPublisher=Kuki Technologies
LicenseFile=license.txt
DefaultDirName={pf}\DeliveryOrder
DefaultGroupName=DeliveryOrder
OutputBaseFilename=DeliveryOrderInstaller
Compression=lzma
SolidCompression=yes
WizardStyle=modern
DisableDirPage=no


[Files]
Source: "app\*"; DestDir: "{app}\app"; Flags: recursesubdirs createallsubdirs
Source: "runtime\*"; DestDir: "{app}\runtime"; Flags: recursesubdirs createallsubdirs
Source: "ADMIN.exe"; DestDir: "{app}"
Source: "CAJERO.exe"; DestDir: "{app}"
Source: "DeliveryOrder.exe"; DestDir: "{app}"


[Tasks]
Name: "servidor"; Description: "Servidor"
Name: "admin"; Description: "Admin"
Name: "cajero"; Description: "Cajero"


[Icons]
Name: "{group}\Servidor"; Filename: "{app}\DeliveryOrder.exe"; Tasks: servidor
Name: "{group}\Admin"; Filename: "{app}\ADMIN.exe"; Tasks: admin
Name: "{group}\Cajero"; Filename: "{app}\CAJERO.exe"; Tasks: cajero

Name: "{commondesktop}\Servidor"; Filename: "{app}\DeliveryOrder.exe"; Tasks: servidor
Name: "{commondesktop}\Admin"; Filename: "{app}\ADMIN.exe"; Tasks: admin
Name: "{commondesktop}\Cajero"; Filename: "{app}\CAJERO.exe"; Tasks: cajero


[Run]
Filename: "{app}\DeliveryOrder.exe"; Description: "Iniciar Servidor"; Flags: nowait postinstall skipifsilent; Tasks: servidor
Filename: "{app}\ADMIN.exe"; Description: "Abrir Admin"; Flags: nowait postinstall skipifsilent; Tasks: admin
Filename: "{app}\CAJERO.exe"; Description: "Abrir Cajero"; Flags: nowait postinstall skipifsilent; Tasks: cajero


[Code]

var
  KeyPage: TInputQueryWizardPage;
  TasksPage: TInputOptionWizardPage;


function InitializeWizard(): Integer;
begin
  // --- PÁGINA DE KEY ---
  KeyPage := CreateInputQueryPage(
    wpSelectDir,
    'Clave de Instalación',
    'Ingresa tu clave',
    'Introduce la clave proporcionada para continuar.'
  );
  KeyPage.Add('Clave:', False);

  // --- PÁGINA DE OPCIONES ---
  TasksPage := CreateInputOptionPage(
    KeyPage.ID,
    'Componentes',
    'Selecciona qué deseas instalar',
    'Elige los accesos que deseas crear:',
    True,
    False
  );

  TasksPage.Add('Servidor');
  TasksPage.Add('Admin');
  TasksPage.Add('Cajero');

  // seleccionados por defecto
  TasksPage.Values[0] := True;
  TasksPage.Values[1] := True;
  TasksPage.Values[2] := True;

  Result := 0;
end;


// VALIDAR CLAVE
function NextButtonClick(CurPageID: Integer): Boolean;
begin
  Result := True;

  if CurPageID = KeyPage.ID then
  begin
    if KeyPage.Values[0] <> 'a1b6b3b4-1622-4301-b41d-ec1fb71ddd07' then
    begin
      MsgBox('Clave inválida. No puedes continuar.', mbError, MB_OK);
      Result := False;
    end;
  end;
end;


// ACTIVAR TASKS SEGÚN CHECKBOXES
procedure CurStepChanged(CurStep: TSetupStep);
begin
  if CurStep = ssInstall then
  begin
    if TasksPage.Values[0] then WizardSelectTasks('servidor', True);
    if TasksPage.Values[1] then WizardSelectTasks('admin', True);
    if TasksPage.Values[2] then WizardSelectTasks('cajero', True);
  end;
end;


// TEXTO FINAL PERSONALIZADO
procedure CurPageChanged(CurPageID: Integer);
begin
  if CurPageID = wpFinished then
  begin
    WizardForm.FinishedLabel.Caption :=
      'Instalación completada correctamente.' + #13#10 +
      'Ahora puedes usar DeliveryOrder.';
  end;
end;