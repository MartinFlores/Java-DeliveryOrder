[Setup]
AppName=DeliveryOrder
AppVersion=1.0
AppPublisher=Kuki Technologies
LicenseFile=license.txt
DefaultDirName={pf}\DeliveryOrder
DefaultGroupName=DeliveryOrder
OutputBaseFilename=DeliveryOrder-Installer-v1.0
Compression=lzma
SolidCompression=yes
WizardStyle=modern
DisableDirPage=no
DisableProgramGroupPage=yes
[Languages]
Name: "spanish"; MessagesFile: "compiler:Languages\Spanish.isl"


[Files]
Source: "app\*"; DestDir: "{app}\app"; Flags: recursesubdirs createallsubdirs
Source: "runtime\*"; DestDir: "{app}\runtime"; Flags: recursesubdirs createallsubdirs
Source: "ADMIN.exe"; DestDir: "{app}"
Source: "CAJERO.exe"; DestDir: "{app}"
Source: "DeliveryOrder.exe"; DestDir: "{app}"


[Icons]
Name: "{group}\Servidor"; Filename: "{app}\DeliveryOrder.exe"
Name: "{group}\Admin"; Filename: "{app}\ADMIN.exe"
Name: "{group}\Cajero"; Filename: "{app}\CAJERO.exe"

Name: "{commondesktop}\Servidor"; Filename: "{app}\DeliveryOrder.exe"
Name: "{commondesktop}\Admin"; Filename: "{app}\ADMIN.exe"
Name: "{commondesktop}\Cajero"; Filename: "{app}\CAJERO.exe"


[Run]
Filename: "{app}\DeliveryOrder.exe"; Description: "Iniciar Servidor"; Flags: nowait postinstall skipifsilent


[Code]

var
  KeyPage: TInputQueryWizardPage;


procedure InitializeWizard();
begin
  KeyPage := CreateInputQueryPage(
    wpSelectDir,
    'Clave de Instalación',
    'Ingresa tu clave',
    'Introduce la clave proporcionada para continuar.'
  );

  KeyPage.Add('Clave:', False);
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


// TEXTO FINAL
procedure CurPageChanged(CurPageID: Integer);
begin
  if CurPageID = wpFinished then
  begin
    WizardForm.FinishedLabel.Caption :=
      'Instalación completada correctamente.' + #13#10 +
      'Ahora puedes usar DeliveryOrder.';
  end;
end;